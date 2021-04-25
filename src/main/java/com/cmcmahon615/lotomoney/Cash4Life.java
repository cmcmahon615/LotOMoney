package com.cmcmahon615.lotomoney;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

public class Cash4Life extends Lottery {

    public Cash4Life(String quickPick) {
        super(2,7);
        if (quickPick.equals("QP")) {
            this.baseNumbers = basePicker();
            this.plusNumber = plusPicker();
        }

    }

    /* Additional constructors, getters, and setters for future feature to simulate lottery strategies
    public Cash4Life() {
        super(2, 2);
    }

    public Cash4Life(HashSet<Integer> baseNumbers) {
        super(2,2);
        this.baseNumbers = baseNumbers;
        this.plusNumber = plusPicker();
    }

    public Cash4Life(Integer plusNumber) {
        super(2,2);
        this.plusNumber = plusNumber;
        this.baseNumbers = basePicker();
    }

    public Cash4Life(HashSet<Integer> baseNumbers, Integer plusNumber) {
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
            result.add(rand.nextInt(60) + 1);
        return result;
    }

    @Override
    public Integer plusPicker() {
        Random rand = new Random();
        return rand.nextInt(4) + 1;
    }

    public static void simulateLottery(Options options, Stats stats) {
        Lottery winningTicket = new Cash4Life("QP");
        stats.drawingsHeld++;
        stats.prizeMoney += stats.jackpot * 1000;
        stats.kPerDay += stats.jackpot;
        if (stats.drawingsHeld % 7 == 0) {
            stats.prizeMoney += stats.fiveOfFive * 1000;
            stats.kPerWeek += stats.fiveOfFive;
        }
        if (stats.drawingsHeld % 365 == 0)
            stats.yearsPassed++;

        // Generate player tickets for this drawing
        ArrayList<Lottery> playerTickets = new ArrayList<>();
        if (options.batchContinuous.size() == 0) {
            playerTickets.add(new Cash4Life("QP"));
            stats.ticketsPurchased++;
            for (int i = 1; i < options.ticketsPerDrawing; i++) {
                Lottery ticket = new Cash4Life("QP");
                if (options.qpContinuous) {
                    // Check to make sure identical tickets aren't created in the batch
                    for (Lottery lottery : playerTickets) {
                        while (ticket.baseNumbers.equals(lottery.baseNumbers) &&
                                ticket.plusNumber.equals(lottery.plusNumber))
                            ticket = new Cash4Life("QP");
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
        System.out.printf("Tickets this Drawing: %s | Drawings: %s | Total Tickets: %s\n",
                df.format(playerTickets.size()), df.format(stats.drawingsHeld), df.format(stats.ticketsPurchased));

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
                stats.jackpot++;
                stats.prizeWinners++;
                stats.wonJackpot = true;
            } else if (size == 5) {
                stats.fiveOfFive++;
                stats.prizeWinners++;
            } else if (size == 4 && lottery.plusNumber.equals(winningTicket.plusNumber)) {
                stats.fourOfFivePlus++;
                stats.prizeWinners++;
                stats.prizeMoney += 2500;
            } else if (size == 4) {
                stats.fourOfFive++;
                stats.prizeWinners++;
                stats.prizeMoney += 500;
            } else if (size == 3 && lottery.plusNumber.equals(winningTicket.plusNumber)) {
                stats.threeOfFivePlus++;
                stats.prizeWinners++;
                stats.prizeMoney += 100;
            } else if (size == 3) {
                stats.threeOfFive++;
                stats.prizeWinners++;
                stats.prizeMoney += 25;
            } else if (size == 2 && lottery.plusNumber.equals(winningTicket.plusNumber)) {
                stats.twoOfFivePlus++;
                stats.prizeWinners++;
                stats.prizeMoney += 10;
            } else if (size == 2) {
                stats.twoOfFive++;
                stats.prizeWinners++;
                stats.prizeMoney += 4;
            } else if (size == 1 && lottery.plusNumber.equals(winningTicket.plusNumber)) {
                stats.oneOfFivePlus++;
                stats.prizeWinners++;
                stats.prizeMoney += 2;
            }
        }
    }

    public static void showPrizes(Stats stats) {
        DecimalFormat df = new DecimalFormat("#,###,###,###");
        DecimalFormat pf = new DecimalFormat(("##.##%"));
        float winPercentage = ((float) stats.prizeWinners / (float) stats.ticketsPurchased);
        System.out.printf("""
                        While playing Cash 4 Life, you have matched:
                         
                        Numbers Matched         Payout                  Frequency
                        5 of 5 + Cash Ball:     $1,000/Day for Life     %s time(s)
                        5 of 5:                 $1,000/Week for Life    %s times
                        4 of 5 + Cash Ball:     $2,500                  %s times
                        4 of 5:                 $500                    %s times
                        3 of 5 + Cash Ball:     $100                    %s times
                        3 of 5:                 $25                     %s times
                        2 of 5 + Cash Ball:     $10                     %s times
                        2 of 5:                 $4                      %s times
                        1 of 5 + Cash Ball:     $2                      %s times
                        
                        Prize winning tickets:  %s
                        Win percentage:         %s
                        
                        $1,000/Day for Life paid:      %s times
                        $1,000/Week for Life paid:     %s times
                        
                        Total non-jackpot prize money: $%s
                                                
                        Thanks for playing Lot O'Money
                        """,
                df.format(stats.jackpot), df.format(stats.fiveOfFive), df.format(stats.fourOfFivePlus),
                df.format(stats.fourOfFive), df.format(stats.threeOfFivePlus), df.format(stats.threeOfFive),
                df.format(stats.twoOfFivePlus), df.format(stats.twoOfFive), df.format(stats.oneOfFivePlus),
                df.format(stats.prizeWinners), pf.format(winPercentage), df.format(stats.kPerDay),
                df.format(stats.kPerWeek), df.format(stats.prizeMoney));
    }
}
