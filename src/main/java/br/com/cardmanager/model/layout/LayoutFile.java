package br.com.cardmanager.model.layout;

import java.time.LocalDate;

public class LayoutFile {
    public record Header(String name, LocalDate generationDate, String batchId, int recordCount) {}

    public record Information(int sequence, String cardNumber) {}

    public record Trailer(String batchId, int recordCount) {}
}