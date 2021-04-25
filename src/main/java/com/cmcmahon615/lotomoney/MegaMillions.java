package com.cmcmahon615.lotomoney;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

@EqualsAndHashCode(callSuper = true)
@Data
public class MegaMillions extends Lottery {

    public MegaMillions(String quickPick) {
        super(2,2);
        if (quickPick.equals("QP")) {
            this.baseNumbers = basePicker();
            this.plusNumber = plusPicker();
        }
    }

    /* Additional constructors, getters, and setters for future feature to simulate lottery strategies
    public MegaMillions() {
        super(2, 2);
    }

    public MegaMillions(HashSet<Integer> baseNumbers) {
        super(2,2);
        this.baseNumbers = baseNumbers;
        this.plusNumber = plusPicker();
    }

    public MegaMillions(Integer plusNumber) {
        super(2,2);
        this.plusNumber = plusNumber;
        this.baseNumbers = basePicker();
    }

    public MegaMillions(HashSet<Integer> baseNumbers, Integer plusNumber) {
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
            result.add(rand.nextInt(70) + 1);
        return result;
    }

    @Override
    public Integer plusPicker() {
        Random rand = new Random();
        return rand.nextInt(25) + 1;
    }

    public static void simulateLottery(Options options, Stats stats) {
        Lottery winningTicket = new MegaMillions("QP");
        stats.drawingsHeld++;
        if (stats.drawingsHeld % 104 == 0)
            stats.yearsPassed++;

        // Determine Power Play number
        Random rand = new Random();
        int multiplier = 1;
        int [] megaplierNumbers = {2, 3, 4, 5};
        if (options.bonusPlay) {
            multiplier = multiplier * megaplierNumbers[rand.nextInt(4)];
        }

        // Generate player tickets for this drawing
        ArrayList<Lottery> playerTickets = new ArrayList<>();
        if (options.batchContinuous.size() == 0) {
            playerTickets.add(new MegaMillions("QP"));
            stats.ticketsPurchased++;
            for (int i = 1; i < options.ticketsPerDrawing; i++) {
                Lottery ticket = new MegaMillions("QP");
                if (options.qpContinuous) {
                    // Check to make sure identical tickets aren't created for one drawing
                    for (Lottery lottery : playerTickets) {
                        while (ticket.baseNumbers.equals(lottery.baseNumbers) &&
                                ticket.plusNumber.equals(lottery.plusNumber))
                            ticket = new MegaMillions("QP");
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
            if (lottery.baseNumbers.equals(winningTicket.baseNumbers) &&
                    lottery.plusNumber.equals(winningTicket.plusNumber)) {
                stats.prizeWinners++;
                stats.jackpot++;
                stats.wonJackpot = true;
            } else if (size == 5) {
                stats.fiveOfFive++;
                stats.prizeWinners++;
                stats.prizeMoney += 1000000 * multiplier;
            } else if (size == 4 && lottery.plusNumber.equals(winningTicket.plusNumber)) {
                stats.fourOfFivePlus++;
                stats.prizeWinners++;
                stats.prizeMoney += 10000 * multiplier;
            } else if (size == 4) {
                stats.fourOfFive++;
                stats.prizeWinners++;
                stats.prizeMoney += 500 * multiplier;
            } else if (size == 3 && lottery.plusNumber.equals(winningTicket.plusNumber)) {
                stats.threeOfFivePlus++;
                stats.prizeWinners++;
                stats.prizeMoney += 200 * multiplier;
            } else if (size == 3) {
                stats.threeOfFive++;
                stats.prizeWinners++;
                stats.prizeMoney += 10 * multiplier;
            } else if (size == 2 && lottery.plusNumber.equals(winningTicket.plusNumber)) {
                stats.twoOfFivePlus++;
                stats.prizeWinners++;
                stats.prizeMoney += 10 * multiplier;
            } else if (size == 1 && lottery.plusNumber.equals(winningTicket.plusNumber)) {
                stats.oneOfFivePlus++;
                stats.prizeWinners++;
                stats.prizeMoney += 4 * multiplier;
            } else if (lottery.plusNumber.equals(winningTicket.plusNumber)) {
                stats.plusOnly++;
                stats.prizeWinners++;
                stats.prizeMoney += 2 * multiplier;
            }
        }
    }

    public static void showPrizes(Stats stats) {
        DecimalFormat df = new DecimalFormat("#,###,###,###");
        DecimalFormat pf = new DecimalFormat(("##.##%"));
        float winPercentage = ((float) stats.prizeWinners / (float) stats.ticketsPurchased);
        System.out.printf("""
                        While playing Mega Millions, you have matched:
                        
                        Numbers Matched         Payout          Frequency
                        5 of 5 + Mega Ball:     Jackpot         %s time(s)
                        5 of 5:                 $1,000,000      %s times
                        4 of 5 + Mega Ball:     $10,000         %s times
                        4 of 5:                 $500            %s times
                        3 of 5 + Mega Ball:     $200            %s times
                        3 of 5:                 $10             %s times
                        2 of 5 + Mega Ball:     $10             %s times
                        1 of 5 + Mega Ball:     $4              %s times
                        Mega Ball only:         $2              %s times
                        
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
