package com.cmcmahon615.lotomoney;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Locale;

public class Options {

    protected String game; // Which lottery is being played
    protected Integer ticketsPerDrawing; // How many tickets each drawing
    protected String playUntil; // Why the simulator will stop
    protected int stopCondition; // Stop thresholds
    protected boolean bonusPlay; // Is player adding the multiplier
    protected boolean qpContinuous; // True = First batch of tickets played on subsequent drawings | False = QuickPick every ticket every drawing
    protected ArrayList<Lottery> batchContinuous = new ArrayList<>(); // Batch of tickets created during simulation if qpContinuous is true.

    /* Options for future feature to simulate lottery strategies
    protected boolean baseContinuous;
    protected boolean plusContinuous;
    */

    public static Options setUpGame() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        Options result = new Options();

        // Determine lottery simulator
        System.out.println("""
                Which lottery would you like to play?
                    1 - Powerball
                    2 - Mega Millions
                    3 - Lotto America
                    4 - Cash 4 Life""");

        Integer choice;
        String sChoice;
        do {
            System.out.print("Enter game number: ");
            choice = tryParse(reader.readLine());
        } while (choice < 1 || choice > 4);

        switch (choice) {
            case 1 -> result.game = "PB";
            case 2 -> result.game = "MM";
            case 3 -> result.game = "LA";
            case 4 -> result.game = "C4L";
        }

        // Determine play Until Settings
        System.out.println("""
                    Play until...
                    1 - I win the jackpot
                    2 - I win at least a certain amount in prizes
                    3 - I have spent a certain amount on tickets
                    4 - A certain amount of years have passed""");
        do {
            System.out.print("Play option: ");
            choice = tryParse(reader.readLine());
        } while (choice < 1 || choice > 4);
        switch (choice) {
            case 1 -> result.playUntil = "JP";
            case 2 -> result.playUntil = "PA";
            case 3 -> result.playUntil = "MS";
            case 4 -> result.playUntil = "YP";
        }
        if (choice == 2) {
            do {
                System.out.print("Target prize money: ");
                choice = tryParse(reader.readLine());
            } while (choice < 1);
        } else if (choice == 3) {
            do {
                System.out.print("Max amount to spend: ");
                choice = tryParse(reader.readLine());
            } while (choice < 1);
        }
        else if (choice == 4) {
            do {
                System.out.print("Years to play: ");
                choice = tryParse(reader.readLine());
            } while (choice < 1);
        }
        result.stopCondition = choice;

        do {
            System.out.print("Number of tickets each drawing: ");
            choice = tryParse(reader.readLine());
        } while (choice < 1);
        result.ticketsPerDrawing = choice;

        System.out.println("""
                    For each drawing...
                    1 - Use QuickPick to pick my numbers every time
                    2 - Use QuickPick to pick my numbers once and use that set continuously""");
        do {
            System.out.print("Drawing option: ");
            choice = tryParse(reader.readLine());
        } while (choice < 1 || choice > 2);
        switch (choice) {
            case 1 -> result.qpContinuous = false;
            case 2 -> result.qpContinuous = true;
        }

        do {
            if (!result.game.equals("C4L")) {
                switch (result.game) {
                    case "PB" -> System.out.print("Add Power Play? (y/n): ");
                    case "MM" -> System.out.print("Add Megaplier? (y/n): ");
                    case "LA" -> System.out.print("Add All Star Bonus (y/n): ");
                }
                sChoice = reader.readLine();
            } else
                sChoice = "N";
        } while(!sChoice.toUpperCase(Locale.ENGLISH).equals("Y") && !sChoice.toUpperCase(Locale.ENGLISH).equals("N"));
        if (sChoice.toUpperCase(Locale.ENGLISH).equals("Y"))
            result.bonusPlay = true;

        reader.close();
        return result;
    }

    public static Integer tryParse(String s) {
        int result;
        try {
            result = Integer.parseInt(s);
        } catch (NumberFormatException nfe) {
            result = 0;
        }
        return result;
    }
}
