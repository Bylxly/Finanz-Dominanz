package Server.State;

import Server.*;
import Server.Field.Property.Property;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class AuctionState extends Thread implements GameState {

    private final Game game;
    private List<Player> activePlayers;
    private Player highestBidder;
    private volatile AtomicBoolean auctionRunning;
    private int currentBid;

    public AuctionState(Game game) {
        this.game = game;
        this.auctionRunning = new AtomicBoolean(true);
        this.activePlayers = new ArrayList<Player>(game.getPlayers());
    }

    @Override
    public void execute() {
        this.start();
        for (Player player : game.getPlayers()) {
            System.out.println(player.getName());
            new Thread(() -> handlePlayerBid(player)).start();
        }
        try {
            this.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void broadcast(Message message) {
        for (Player player : activePlayers) {
            player.sendObject(message);
        }
    }

    private void handlePlayerBid(Player player) {
        try {
            while (auctionRunning.get() && contains(player)) {
                String msg = player.recieveMessage();
                if (msg.equalsIgnoreCase("QUIT_AUCTION")) {
                    if (player != highestBidder || activePlayers.size() == 1) {
                        removePlayerFromList(player);
                        if (auctionRunning.get()) {
                            player.sendObject(new Message(MsgType.QUIT_AUCTION, "You have quit the auction."));
                        }
                    } else {player.sendObject(new Message(MsgType.INFO, "You can't quit the auction as the highest bidder."));}
                } else {
                    try {
                        int newBid = Integer.parseInt(msg);
                        synchronized (this) {
                            if (newBid > currentBid) {
                                if (newBid <= player.getMoney()) {
                                    currentBid = newBid;
                                    highestBidder = player;
                                    broadcast(new Message(MsgType.NEW_BID, String.valueOf(newBid)));
                                    if (activePlayers.size() == 1) {
                                        this.notifyAll();
                                    }
                                } else {
                                    player.sendObject(new Message(MsgType.INFO, "You don't have enough money."));
                                }
                            } else {
                                player.sendObject(new Message(MsgType.INFO, "Your bid is too low."));
                            }
                        }
                    } catch (NumberFormatException e) {
                        player.sendObject(new Message(MsgType.INFO, "Invalid bid. Please enter a number or 'QUIT'."));
                    }
                }
            }
        } catch (Exception e) {
            removePlayerFromList(player);
            player.sendObject(new Message(MsgType.INFO, "Error processing your bid."));
        }
    }

    @Override
    public void run() {
        broadcast(new Message(MsgType.DO_AUCTION, "Auction started! Place your bids."));
        synchronized (this) {
            while (auctionRunning.get() && (activePlayers.size() > 1 || getCurrentBid() == 0)) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e.getMessage());
                }
            }
        }
        System.out.println("Auction stopped.");
        endAuction();
    }

    private void endAuction() {
        auctionRunning.set(false);
        if (highestBidder != null && activePlayers.contains(highestBidder)) {
            Property property = (Property) game.getActivePlayer().getCurrentField();
            if (GameUtilities.checkIfEnoughMoney(highestBidder, currentBid)) {
                property.buy(highestBidder, currentBid);
                broadcast(new Message(MsgType.END_AUCTION, "Auction ended! Winner: " + highestBidder.getName() + " with bid " + currentBid));
            } else {
                broadcast(new Message(MsgType.END_AUCTION, "Auction ended! Winner does not have enough money."));
            }
        } else {
            broadcast(new Message(MsgType.END_AUCTION, "Auction ended with no valid bids."));
        }
    }

    public synchronized int getCurrentBid() {
        return currentBid;
    }

    public synchronized void setCurrentBid(int currentBid) {
        this.currentBid = currentBid;
    }

    public synchronized boolean contains(Player player) {
        return activePlayers.contains(player);
    }

    public synchronized void removePlayerFromList(Player player) {
        activePlayers.remove(player);
        this.notifyAll();
    }
}
