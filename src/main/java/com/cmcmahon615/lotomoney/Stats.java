package com.cmcmahon615.lotomoney;

import java.text.DecimalFormat;

public class Stats {
    protected int ticketsPurchased = 0;
    protected int drawingsHeld = 0;
    protected int prizeMoney = 0;
    protected boolean wonJackpot;
    protected int prizeWinners = 0; // Number of tickets eligible for a prize
    protected int yearsPassed = 0;

    // Prize Counters
    protected int jackpot = 0;
    protected int fiveOfFive = 0;
    protected int fourOfFivePlus = 0;
    protected int fourOfFive = 0;
    protected int threeOfFivePlus = 0;
    protected int threeOfFive = 0;
    protected int twoOfFivePlus = 0;
    protected int twoOfFive = 0; // Only in Cash 4 Life
    protected int oneOfFivePlus = 0;
    protected int plusOnly = 0; // Not in Cash 4 Life
    protected int kPerDay = 0; // Only in Cash 4 Life - For when this prize pays out
    protected int kPerWeek = 0; // Only in Cash 4 Life - For when this prize pays out

    public int calculateMoneySpent (Options options) {
        if (options.bonusPlay) {
            switch (options.game) {
                case "PB":
                    return ticketsPurchased * (Powerball.ticketCost + 1);
                case "MM":
                    return ticketsPurchased * (MegaMillions.ticketCost + 1);
                case "LA":
                    return ticketsPurchased * (LottoAmerica.ticketCost + 1);
                case "C4L":
                    return ticketsPurchased * (Cash4Life.ticketCost + 1);
            }
        }
        else
            switch (options.game) {
                case "PB":
                    return ticketsPurchased * (Powerball.ticketCost);
                case "MM":
                    return ticketsPurchased * (MegaMillions.ticketCost);
                case "LA":
                    return ticketsPurchased * (LottoAmerica.ticketCost);
                case "C4L":
                    return ticketsPurchased * (Cash4Life.ticketCost);
            }
        return 0;
    }
    public void reportMoneySpent(Options options) {
        DecimalFormat df = new DecimalFormat("#,###,###,###");
        if (options.bonusPlay)
            System.out.println("Spent on tickets: $" + df.format(calculateMoneySpent(options)));
        else
            System.out.println("Spent on tickets: $" + df.format(calculateMoneySpent(options)));

    }

    public void reportTimePlayed(Options options) {
        DecimalFormat df = new DecimalFormat("#,###,###,###");
        int weeks = 0;
        switch (options.game) {
            case "PB" -> weeks = drawingsHeld / Powerball.drawingsPerWeek;
            case "MM" -> weeks = drawingsHeld / MegaMillions.drawingsPerWeek;
            case "LA" -> weeks = drawingsHeld / LottoAmerica.drawingsPerWeek;
            case "C4L" -> weeks = drawingsHeld / Cash4Life.drawingsPerWeek;
        }
        int years = weeks / 52;
        int months = (weeks % 52) / 4;

        System.out.printf("""
            Time played
            Years: %s
            Months: %d
            Weeks: %d""", df.format(years), months, (months % 4));
    }
}
