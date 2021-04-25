package com.cmcmahon615.lotomoney;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;

@Data
@NoArgsConstructor
public abstract class Lottery implements TicketGenerator {
    protected static int ticketCost;
    protected static int drawingsPerWeek;
    protected HashSet<Integer> baseNumbers;
    protected Integer plusNumber;

    public Lottery(Integer ticketCost, Integer drawingsPerWeek) {
        Lottery.ticketCost = ticketCost;
        Lottery.drawingsPerWeek = drawingsPerWeek;
    }
}
