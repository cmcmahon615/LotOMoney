package com.cmcmahon615.lotomoney;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

public class Powerball extends Lottery {

    public Powerball(String quickPick) {
        super(2,2);
        if (quickPick.equals("QP")) {
            this.baseNumbers = basePicker();
            this.plusNumber = plusPicker();
        }
    }

    /* Additional constructors, getters, and setters for future feature to simulate lottery strategies
    public Powerball() {
        super(2, 2);
    }

    public Powerball(HashSet<Integer> baseNumbers) {
        super(2,2);
        this.baseNumbers = baseNumbers;
        this.plusNumber = plusPicker();
    }

    public Powerball(Integer plusNumber) {
        super(2,2);
        this.plusNumber = plusNumber;
        this.baseNumbers = basePicker();
    }

    public Powerball(HashSet<Integer> baseNumbers, Integer plusNumber) {
        super(2,2);
        this.baseNumbers = baseNumbers;
        this.plusNumber = plusNumber;
    }

    // Getters & Setters
    public HashSet<Integer> getbaseNumbers() {
        return baseNumbers;
    }

    public Integer getplusNumber() {
        return plusNumber;
    }

    public void setbaseNumbers(HashSet<Integer> baseNumbers) {
        this.baseNumbers = baseNumbers;
    }

    public void setplusNumber(Integer plusNumber) {
        this.plusNumber = plusNumber;
    }*/

    // Methods
    @Override
    public HashSet<Integer> basePicker() {
        HashSet<Integer> result = new HashSet<>();
        Random rand = new Random();
        while (result.size() < 5)
            result.add(rand.nextInt(69) + 1);
        return result;
    }

    @Override
    public Integer plusPicker() {
        Random rand = new Random();
        return rand.nextInt(26) + 1;
    }

    public static void simulateLottery(Options options, Stats stats) {
        Lottery winningTicket = new Powerball("QP");
        stats.drawingsHeld++;
        if (stats.drawingsHeld % 104 == 0)
            stats.yearsPassed++;

        // Determine Power Play number
        Random rand = new Random();
        int multiplier = 1;
        int [] powerPlayNumbers = {2, 3, 4, 5, 10};
        if (options.bonusPlay) {
            multiplier = multiplier * powerPlayNumbers[rand.nextInt(5)];
        }

        // Generate player tickets for this drawing
        ArrayList<Lottery> playerTickets = new ArrayList<>();
        if (options.batchContinuous.size() == 0) {
            playerTickets.add(new Powerball("QP"));
            stats.ticketsPurchased++;
            for (int i = 1; i < options.ticketsPerDrawing; i++) {
                Lottery ticket = new Powerball("QP");
                if (options.qpContinuous) {
                    // Check to make sure identical tickets aren't created for one drawing
                    for (Lottery lottery : playerTickets) {
                        while (ticket.baseNumbers.equals(lottery.baseNumbers) &&
                                ticket.plusNumber.equals(lottery.plusNumber))
                            ticket = new Powerball("QP");
                    }
                }
                playerTickets.add(ticket);
                stats.ticketsPurchased++;
            }
        }

        // Synchronize batchContinuous and playerTickets
        if (options.qpContinuous && options.batchContinuous.size() == 0)
            options.batchContinuous.addAll(playerTickets);
        else if (options.qpContinuous) {
            playerTickets.addAll(options.batchContinuous);
            stats.ticketsPurchased += options.batchContinuous.size();
        }

        // Check for prizes
        DecimalFormat df = new DecimalFormat("#,###,###,###");
        /* Debug print
        System.out.println("playerTickets size: " + playerTickets.size());
        System.out.println("qpContinuous: " + options.qpContinuous);
        System.out.println("batchContinuous: " + options.batchContinuous.size());
        for (Lottery lottery : options.batchContinuous)
            System.out.println("Batch ticket: " + lottery.baseNumbers + " | " + lottery.plusNumber);*/
        System.out.printf("Tickets this Drawing: %s | Multiplier: %d | Drawings: %s | Total Tickets: %s\n",
            df.format(playerTickets.size()), multiplier, df.format(stats.drawingsHeld), df.format(stats.ticketsPurchased));
        for (Lottery lottery : playerTickets) {
            // Create a list of matching numbers
            ArrayList<Integer> baseMatches = new ArrayList<>();
            for (Integer i : lottery.baseNumbers)
                if (winningTicket.baseNumbers.contains(i))
                    baseMatches.add(i);
            // Check size of list to determine prize eligibility
            int size = baseMatches.size();
            if (size == 0 && !lottery.plusNumber.equals(winningTicket.plusNumber))
                continue;
            // Determine prize
            if (lottery.baseNumbers.equals(winningTicket.baseNumbers) &&
                    lottery.plusNumber.equals(winningTicket.plusNumber)) {
                stats.prizeWinners++;
                stats.jackpot++;
                stats.wonJackpot = true;
            } else if (size == 5) {
                stats.fiveOfFive++;
                stats.prizeWinners++;
                if (options.bonusPlay)
                    stats.prizeMoney =+ 2000000;
                else
                    stats.prizeMoney += 1000000;
            } else if (size == 4 && lottery.plusNumber.equals(winningTicket.plusNumber)) {
                stats.fourOfFivePlus++;
                stats.prizeWinners++;
                stats.prizeMoney += 50000 * multiplier;
            } else if (size == 4) {
                stats.fourOfFive++;
                stats.prizeWinners++;
                stats.prizeMoney += 100 * multiplier;
            } else if (size == 3 && lottery.plusNumber.equals(winningTicket.plusNumber)) {
                stats.threeOfFivePlus++;
                stats.prizeWinners++;
                stats.prizeMoney += 100 * multiplier;
            } else if (size == 3) {
                stats.threeOfFive++;
                stats.prizeWinners++;
                stats.prizeMoney += 7 * multiplier;
            } else if (size == 2 && lottery.plusNumber.equals(winningTicket.plusNumber)) {
                stats.twoOfFivePlus++;
                stats.prizeWinners++;
                stats.prizeMoney += 7 * multiplier;
            } else if (size == 1 && lottery.plusNumber.equals(winningTicket.plusNumber)) {
                stats.oneOfFivePlus++;
                stats.prizeWinners++;
                stats.prizeMoney += 4 * multiplier;
            } else if (lottery.plusNumber.equals(winningTicket.plusNumber)) {
                stats.plusOnly++;
                stats.prizeWinners++;
                stats.prizeMoney += 4 * multiplier;
            }
        }
    }

    public static void showPrizes(Stats stats) {
        DecimalFormat df = new DecimalFormat("#,###,###,###");
        DecimalFormat pf = new DecimalFormat(("##.##%"));
        float winPercentage = ((float) stats.prizeWinners / (float) stats.ticketsPurchased);
        System.out.printf("""
                        While playing Powerball, you have matched: 
                        
                        Numbers Matched         Payout          Frequency
                        5 of 5 + Powerball:     Jackpot         %s time(s)
                        5 of 5:                 $1,000,000      %s times
                        4 of 5 + Powerball:     $50,000         %s times
                        4 of 5:                 $100            %s times
                        3 of 5 + Powerball:     $100            %s times
                        3 of 5:                 $7              %s times
                        2 of 5 + Powerball:     $7              %s times
                        1 of 5 + Powerball:     $4              %s times
                        Powerball only:         $4              %s times
                        
                        Prize winning tickets:  %s
                        Win percentage:         %s
                        
                        Total non-jackpot prize money: $%s
                                                
                        Thanks for playing Lot O'Money!!!
                        """,
                df.format(stats.jackpot), df.format(stats.fiveOfFive), df.format(stats.fourOfFivePlus),
                df.format(stats.fourOfFive), df.format(stats.threeOfFivePlus), df.format(stats.threeOfFive),
                df.format(stats.twoOfFivePlus), df.format(stats.oneOfFivePlus), df.format(stats.plusOnly),
                df.format(stats.prizeWinners), pf.format(winPercentage), df.format(stats.prizeMoney));
    }
}
