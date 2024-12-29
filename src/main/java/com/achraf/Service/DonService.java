package com.achraf.services;

import com.achraf.DBConnection;
import com.achraf.models.Don;
import com.achraf.models.Donateur;
import com.achraf.utils.EmailUtil;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DonService {
    private DonateurService donateurService;

    public DonService() {
        this.donateurService = new DonateurService();
    }

    public List<Don> getDonsByDonateurId(int donateurId) throws SQLException {
        List<Don> dons = new ArrayList<>();
        String query = "SELECT * FROM dons WHERE donateur_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, donateurId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Don don = new Don(
                            rs.getInt("id"),
                            rs.getInt("donateur_id"),
                            rs.getDouble("montant"),
                            rs.getDate("date_don").toLocalDate()
                    );
                    dons.add(don);
                }
            }
        }
        return dons;
    }

    public void addDon(int donateurId, double montant) throws SQLException {
        LocalDate dateDon = LocalDate.now(); // Utiliser la date actuelle

        String query = "INSERT INTO dons (donateur_id, montant, date_don) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, donateurId);
            stmt.setDouble(2, montant);
            stmt.setDate(3, Date.valueOf(dateDon));
            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int donId = generatedKeys.getInt(1);

                    // Envoyer un email de remerciement avec le reçu en pièce jointe
                    Donateur donateur = donateurService.getDonateurById(donateurId);
                    String subject = "Merci pour votre don!";
                    String body = "Cher " + donateur.getNom() + ",\n\nMerci beaucoup pour votre généreux don de " + montant + " EUR.\n\nVotre soutien est grandement apprécié!\n\nVous trouverez ci-joint le reçu de votre don.\n\nCordialement,\nL'Association";
                    String attachmentPath = generateRecuPDF(new Don(donId, donateurId, montant, dateDon));
                    if (attachmentPath != null) {
                        EmailUtil.sendEmailWithAttachment(donateur.getEmail(), subject, body, attachmentPath);
                    } else {
                        System.err.println("Erreur lors de la génération du reçu PDF.");
                    }
                } else {
                    throw new SQLException("Échec de la création du don, aucun ID obtenu.");
                }
            }
        }
    }

    private String generateRecuPDF(Don don) {
        try {
            Donateur donateur = donateurService.getDonateurById(don.getDonateurId());

            String fileName = "recu_don_" + don.getId() + ".pdf";
            String userHome = System.getProperty("user.home");
            Path downloadPath = Paths.get(userHome, "Downloads", fileName);

            // Création du document PDF
            PDDocument document = new PDDocument();
            PDPage page = new PDPage();
            document.addPage(page);

            PDPageContentStream contentStream = new PDPageContentStream(document, page);

            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
            contentStream.newLineAtOffset(25, 750);
            contentStream.showText("Reçu de Don");
            contentStream.endText();

            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA, 12);
            contentStream.newLineAtOffset(25, 725);
            contentStream.showText("ID du Don: " + don.getId());
            contentStream.endText();

            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA, 12);
            contentStream.newLineAtOffset(25, 700);
            contentStream.showText("Nom du Donateur: " + donateur.getNom());
            contentStream.endText();

            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA, 12);
            contentStream.newLineAtOffset(25, 675);
            contentStream.showText("Montant: " + don.getMontant());
            contentStream.endText();

            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA, 12);
            contentStream.newLineAtOffset(25, 650);
            contentStream.showText("Date de Don: " + don.getDateDon());
            contentStream.endText();

            contentStream.close();
            document.save(downloadPath.toString());
            document.close();

            System.out.println("Reçu généré avec succès : " + downloadPath);
            return downloadPath.toString();
        } catch (IOException  e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean donateurExists(int donateurId) {
        String query = "SELECT 1 FROM donateurs WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, donateurId);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public double getTotalDons() throws SQLException {
        String query = "SELECT montant FROM total_dons WHERE id = 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getDouble("montant");
            } else {
                throw new SQLException("Erreur : Total des dons non trouvé.");
            }
        }
    }

    public void updateTotalDons(double totalDons) throws SQLException {
        String query = "UPDATE total_dons SET montant = ? WHERE id = 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setDouble(1, totalDons);
            stmt.executeUpdate();
        }
    }

    public List<Don> getDons() {
        List<Don> dons = new ArrayList<>();
        String query = "SELECT * FROM dons";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("id");
                int donateurId = rs.getInt("donateur_id");
                double montant = rs.getDouble("montant");
                java.sql.Date sqlDateDon = rs.getDate("date_don");
                LocalDate dateDon = sqlDateDon != null ? sqlDateDon.toLocalDate() : null;
                dons.add(new Don(id, donateurId, montant, dateDon));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dons;
    }

    public void deleteDon(int id) {
        String query = "DELETE FROM dons WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateDon(int id, int donateurId, double montant, LocalDate dateDon) throws SQLException {
        String query = "UPDATE dons SET donateur_id = ?, montant = ?, date_don = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, donateurId);
            stmt.setDouble(2, montant);
            stmt.setDate(3, java.sql.Date.valueOf(dateDon));
            stmt.setInt(4, id);
            stmt.executeUpdate();
        }
    }
}
