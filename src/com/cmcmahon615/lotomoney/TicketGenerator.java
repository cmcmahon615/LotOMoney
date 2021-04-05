package com.cmcmahon615.lotomoney;

import java.util.HashSet;

public interface TicketGenerator {
    HashSet<Integer> basePicker();
    Integer plusPicker();
}
