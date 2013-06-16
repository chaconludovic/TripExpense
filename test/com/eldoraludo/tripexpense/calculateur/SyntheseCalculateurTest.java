package com.eldoraludo.tripexpense.calculateur;

import com.eldoraludo.tripexpense.dto.SyntheseDTO;
import com.eldoraludo.tripexpense.entite.Depense;
import com.eldoraludo.tripexpense.entite.Emprunt;
import com.eldoraludo.tripexpense.entite.Participant;
import com.eldoraludo.tripexpense.entite.TypeDeDepense;

import org.joda.time.DateTime;
import org.junit.*;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SyntheseCalculateurTest {

    public static void main(String[] args) {
        aucune_depense_aucun_participant();
        quatre_depenses_cinq_participants();
        une_depense_deux_participants();
        tester_lajout_d_un_emprunt();
        tester_lajout_d_un_emprunt_avec_dette_existante();
    }


    public static void aucune_depense_aucun_participant() {
        List<Depense> depenses = new ArrayList<Depense>();
        List<Emprunt> emprunts = new ArrayList<Emprunt>();
        List<Participant> participants = new ArrayList<Participant>();
        SyntheseCalculateur syntheseCalculateur = new SyntheseCalculateur(
                emprunts, depenses, participants);
        List<SyntheseDTO> synthese = syntheseCalculateur.run();
        Assert.assertEquals(0, synthese.size());
    }

    @Test
    public void une_depense_un_participant() {
        List<Depense> depenses = new ArrayList<Depense>();
        List<Participant> participants = new ArrayList<Participant>();
        Participant participant = Participant.newBuilder().withId(1)
                .withDateArrive(DateTime.parse("2013-01-01"))
                .withDateDepart(DateTime.parse("2013-01-05")).withNom("part1")
                .withProjetId(1).build();
        participants.add(participant);
        Depense depense = Depense.newBuilder().withId(1).withMontant(50.0)
                .withDateDebut(DateTime.parse("2013-01-01"))
                .withDateFin(DateTime.parse("2013-01-05"))
                .withNomDepense("dep 1").withParticipantId(participant.getId())
                .withTypeDeDepense(TypeDeDepense.COURSE).withProjetId(1)
                .build();
        depenses.add(depense);
        SyntheseCalculateur syntheseCalculateur = new SyntheseCalculateur(
                null, depenses, participants);
        List<SyntheseDTO> synthese = syntheseCalculateur.run();
        Assert.assertEquals(0, synthese.size());
    }

    public static void une_depense_deux_participants() {
        List<Depense> depenses = new ArrayList<Depense>();
        List<Participant> participants = new ArrayList<Participant>();
        Participant participant1 = Participant.newBuilder().withId(1)
                .withDateArrive(DateTime.parse("2013-01-01"))
                .withDateDepart(DateTime.parse("2013-01-05")).withNom("part1")
                .withProjetId(1).build();
        participants.add(participant1);
        Participant participant2 = Participant.newBuilder().withId(2)
                .withDateArrive(DateTime.parse("2013-01-01"))
                .withDateDepart(DateTime.parse("2013-01-05")).withNom("part2")
                .withProjetId(1).build();
        participants.add(participant2);
        Depense depense = Depense.newBuilder().withId(1).withMontant(50.0)
                .withDateDebut(DateTime.parse("2013-01-01"))
                .withDateFin(DateTime.parse("2013-01-05"))
                .withNomDepense("dep 1")
                .withParticipantId(participant1.getId())
                .withTypeDeDepense(TypeDeDepense.COURSE).withProjetId(1)
                .build();
        depenses.add(depense);
        SyntheseCalculateur syntheseCalculateur = new SyntheseCalculateur(
                null, depenses, participants);
        List<SyntheseDTO> synthese = syntheseCalculateur.run();
        Assert.assertEquals(1, synthese.size());
        SyntheseDTO syntheseDTO = synthese.get(0);
        Assert.assertEquals(participant2.getNom(), syntheseDTO.getParticipant()
                .getNom());
        Assert.assertEquals(participant1.getNom(), syntheseDTO.getDepenseur()
                .getNom());
        Assert.assertEquals(Double.valueOf(25), syntheseDTO.getMontant());
    }

    public static void quatre_depenses_cinq_participants() {
        List<Depense> depenses = new ArrayList<Depense>();
        List<Participant> participants = new ArrayList<Participant>();
        Participant ludo = Participant.newBuilder().withId(1)
                .withDateArrive(DateTime.parse("2013-01-19"))
                .withDateDepart(DateTime.parse("2013-01-20")).withNom("ludo")
                .withProjetId(1).build();
        participants.add(ludo);
        Participant robyn = Participant.newBuilder().withId(2)
                .withDateArrive(DateTime.parse("2013-01-20"))
                .withDateDepart(DateTime.parse("2013-01-20")).withNom("robyn")
                .withProjetId(1).build();
        participants.add(robyn);
        Participant lolo = Participant.newBuilder().withId(3)
                .withDateArrive(DateTime.parse("2013-01-19"))
                .withDateDepart(DateTime.parse("2013-01-20")).withNom("lolo")
                .withProjetId(1).build();
        participants.add(lolo);
        Participant benji = Participant.newBuilder().withId(4)
                .withDateArrive(DateTime.parse("2013-01-19"))
                .withDateDepart(DateTime.parse("2013-01-21")).withNom("benji")
                .withProjetId(1).build();
        participants.add(benji);
        Participant charli = Participant.newBuilder().withId(5)
                .withDateArrive(DateTime.parse("2013-01-19"))
                .withDateDepart(DateTime.parse("2013-01-21")).withNom("charli")
                .withProjetId(1).build();
        participants.add(charli);
        Depense depenseLudo = Depense.newBuilder().withId(1).withMontant(50.0)
                .withDateDebut(DateTime.parse("2013-01-20"))
                .withDateFin(DateTime.parse("2013-01-20"))
                .withNomDepense("dep ludo 50").withParticipantId(ludo.getId())
                .withTypeDeDepense(TypeDeDepense.COURSE).withProjetId(1)
                .build();
        depenses.add(depenseLudo);
        Depense depenseLolo = Depense.newBuilder().withId(2).withMontant(50.0)
                .withDateDebut(DateTime.parse("2013-01-20"))
                .withDateFin(DateTime.parse("2013-01-20"))
                .withNomDepense("dep lolo 50").withParticipantId(lolo.getId())
                .withTypeDeDepense(TypeDeDepense.COURSE).withProjetId(1)
                .build();
        depenses.add(depenseLolo);
        Depense depenseBenji = Depense.newBuilder().withId(3)
                .withMontant(140.0).withDateDebut(DateTime.parse("2013-01-19"))
                .withDateFin(DateTime.parse("2013-01-21"))
                .withNomDepense("dep benji 140")
                .withParticipantId(benji.getId())
                .withTypeDeDepense(TypeDeDepense.COURSE).withProjetId(1)
                .build();
        depenses.add(depenseBenji);
        Depense depenseCharli = Depense.newBuilder().withId(4)
                .withMontant(50.0).withDateDebut(DateTime.parse("2013-01-19"))
                .withDateFin(DateTime.parse("2013-01-19"))
                .withNomDepense("dep charli 50")
                .withParticipantId(charli.getId())
                .withTypeDeDepense(TypeDeDepense.COURSE).withProjetId(1)
                .build();
        depenses.add(depenseCharli);
        SyntheseCalculateur syntheseCalculateur = new SyntheseCalculateur(
                null, depenses, participants);
        Map<DateTime, Set<Participant>> dateParticipantMap = syntheseCalculateur
                .buildDateParticipantMap(participants);
        Assert.assertEquals(4,
                dateParticipantMap.get(DateTime.parse("2013-01-19")).size());
        Assert.assertEquals(5,
                dateParticipantMap.get(DateTime.parse("2013-01-20")).size());
        Assert.assertEquals(2,
                dateParticipantMap.get(DateTime.parse("2013-01-21")).size());
        Map<Depense, Map<Participant, Double>> depenseParticipantsMap = syntheseCalculateur
                .extractionParticipantParDepense(depenses, participants,
                        dateParticipantMap);
        Assert.assertEquals(4, depenseParticipantsMap.get(depenseLudo).size());
        Assert.assertEquals(4, depenseParticipantsMap.get(depenseLolo).size());
        Assert.assertEquals(4, depenseParticipantsMap.get(depenseBenji).size());
        Assert.assertEquals(3, depenseParticipantsMap.get(depenseCharli).size());
        Map<Participant, Map<Participant, Double>> dette = syntheseCalculateur
                .extractionDetteParParticipant(depenseParticipantsMap,
                        participants);
        Map<Participant, Map<Participant, Double>> detteNettoyer = syntheseCalculateur
                .nettoyageDesRelationsSymetriques(dette);
        // ludo
        Assert.assertEquals(2, detteNettoyer.get(ludo).size());
        Assert.assertEquals(2.5, detteNettoyer.get(ludo).get(charli), 1);
        Assert.assertEquals(15.0, detteNettoyer.get(ludo).get(benji)
                .doubleValue(), 1);
        // lolo
        Assert.assertEquals(2, detteNettoyer.get(lolo).size());
        Assert.assertEquals(2.5, detteNettoyer.get(lolo).get(charli), 1);
        Assert.assertEquals(15.0, detteNettoyer.get(lolo).get(benji)
                .doubleValue(), 1);
        // robyn
        Assert.assertEquals(3, detteNettoyer.get(robyn).size());
        Assert.assertEquals(10.0, detteNettoyer.get(robyn).get(lolo), 1);
        Assert.assertEquals(10.0, detteNettoyer.get(robyn).get(ludo)
                .doubleValue(), 1);
        Assert.assertEquals(13.0, detteNettoyer.get(robyn).get(benji)
                .doubleValue(), 1);
        // charli
        Assert.assertEquals(1, detteNettoyer.get(charli).size());
        Assert.assertEquals(24.0, detteNettoyer.get(charli).get(benji), 1);

        Map<Participant, Map<Participant, Double>> detteSimplifie = syntheseCalculateur
                .extractionDetteSimplifierParParticipant(detteNettoyer);
        // ludo
        Assert.assertEquals(1, detteSimplifie.get(ludo).size());
        Assert.assertEquals(7, detteSimplifie.get(ludo).get(benji), 1);
        // lolo
        Assert.assertEquals(1, detteSimplifie.get(lolo).size());
        Assert.assertEquals(7, detteSimplifie.get(lolo).get(benji)
                .doubleValue(), 1);
        // charli
        Assert.assertEquals(1, detteSimplifie.get(charli).size());
        Assert.assertEquals(21, detteSimplifie.get(charli).get(benji), 1);
        // robyn
        Assert.assertEquals(1, detteSimplifie.get(robyn).size());
        Assert.assertEquals(33, detteSimplifie.get(robyn).get(benji)
                .doubleValue(), 1);
    }

    public static void tester_lajout_d_un_emprunt() {


        List<Participant> participants = new ArrayList<Participant>();
        Participant emprunteur = Participant.newBuilder().withId(1)
                .withDateArrive(DateTime.parse("2013-01-01"))
                .withDateDepart(DateTime.parse("2013-01-05")).withNom("emprunteur")
                .withProjetId(1).build();
        participants.add(emprunteur);
        Participant participant = Participant.newBuilder().withId(2)
                .withDateArrive(DateTime.parse("2013-01-01"))
                .withDateDepart(DateTime.parse("2013-01-05")).withNom("participant")
                .withProjetId(1).build();
        participants.add(participant);
        List<Emprunt> emprunts = new ArrayList<Emprunt>();
        Emprunt emprunt = Emprunt.newBuilder().withDateEmprunt(DateTime.now()).withNomEmprunt("emprunt")
                .withEmprunteurId(emprunteur.getId()).withParticipantId(participant.getId()).withMontant(500.0).withProjetId(1).withId(1).build();
        emprunts.add(emprunt);
        SyntheseCalculateur syntheseCalculateur = new SyntheseCalculateur(emprunts, null, participants);

        Map<Participant, Map<Participant, Double>> resultats = syntheseCalculateur
                .gestionEmprunt(null, emprunts, participants);
        Assert.assertNotNull(resultats);
        Assert.assertEquals(1, resultats.size());
        Assert.assertNotNull(resultats.keySet().iterator().next());
        Participant emprunteurATrouver = resultats.keySet().iterator().next();
        Assert.assertEquals(emprunteur, emprunteurATrouver);
        Assert.assertEquals(1, resultats.get(emprunteurATrouver).size());
        Assert.assertEquals(participant, resultats.get(emprunteurATrouver).keySet().iterator().next());
        Double montantAVerifier = resultats.get(emprunteurATrouver).get(participant);
        Assert.assertEquals(Double.valueOf(500.0), montantAVerifier);
    }


    public static void tester_lajout_d_un_emprunt_avec_dette_existante() {
        List<Depense> depenses = new ArrayList<Depense>();
        List<Participant> participants = new ArrayList<Participant>();
        Participant ludo = Participant.newBuilder().withId(1)
                .withDateArrive(DateTime.parse("2013-01-19"))
                .withDateDepart(DateTime.parse("2013-01-20")).withNom("ludo")
                .withProjetId(1).build();
        participants.add(ludo);
        Participant robyn = Participant.newBuilder().withId(2)
                .withDateArrive(DateTime.parse("2013-01-20"))
                .withDateDepart(DateTime.parse("2013-01-20")).withNom("robyn")
                .withProjetId(1).build();
        participants.add(robyn);
        Participant lolo = Participant.newBuilder().withId(3)
                .withDateArrive(DateTime.parse("2013-01-19"))
                .withDateDepart(DateTime.parse("2013-01-20")).withNom("lolo")
                .withProjetId(1).build();
        participants.add(lolo);
        Participant benji = Participant.newBuilder().withId(4)
                .withDateArrive(DateTime.parse("2013-01-19"))
                .withDateDepart(DateTime.parse("2013-01-21")).withNom("benji")
                .withProjetId(1).build();
        participants.add(benji);
        Participant charli = Participant.newBuilder().withId(5)
                .withDateArrive(DateTime.parse("2013-01-19"))
                .withDateDepart(DateTime.parse("2013-01-21")).withNom("charli")
                .withProjetId(1).build();
        participants.add(charli);
        Depense depenseLudo = Depense.newBuilder().withId(1).withMontant(50.0)
                .withDateDebut(DateTime.parse("2013-01-20"))
                .withDateFin(DateTime.parse("2013-01-20"))
                .withNomDepense("dep ludo 50").withParticipantId(ludo.getId())
                .withTypeDeDepense(TypeDeDepense.COURSE).withProjetId(1)
                .build();
        depenses.add(depenseLudo);
        Depense depenseLolo = Depense.newBuilder().withId(2).withMontant(50.0)
                .withDateDebut(DateTime.parse("2013-01-20"))
                .withDateFin(DateTime.parse("2013-01-20"))
                .withNomDepense("dep lolo 50").withParticipantId(lolo.getId())
                .withTypeDeDepense(TypeDeDepense.COURSE).withProjetId(1)
                .build();
        depenses.add(depenseLolo);
        Depense depenseBenji = Depense.newBuilder().withId(3)
                .withMontant(140.0).withDateDebut(DateTime.parse("2013-01-19"))
                .withDateFin(DateTime.parse("2013-01-21"))
                .withNomDepense("dep benji 140")
                .withParticipantId(benji.getId())
                .withTypeDeDepense(TypeDeDepense.COURSE).withProjetId(1)
                .build();
        depenses.add(depenseBenji);
        Depense depenseCharli = Depense.newBuilder().withId(4)
                .withMontant(50.0).withDateDebut(DateTime.parse("2013-01-19"))
                .withDateFin(DateTime.parse("2013-01-19"))
                .withNomDepense("dep charli 50")
                .withParticipantId(charli.getId())
                .withTypeDeDepense(TypeDeDepense.COURSE).withProjetId(1)
                .build();
        depenses.add(depenseCharli);

        List<Emprunt> emprunts = new ArrayList<Emprunt>();
        Emprunt emprunt = Emprunt.newBuilder().withDateEmprunt(DateTime.now()).withNomEmprunt("emprunt")
                .withEmprunteurId(ludo.getId()).withParticipantId(benji.getId()).withMontant(500.0).withProjetId(1).withId(1).build();
        emprunts.add(emprunt);


        SyntheseCalculateur syntheseCalculateur = new SyntheseCalculateur(
                emprunts, depenses, participants);
        Map<DateTime, Set<Participant>> dateParticipantMap = syntheseCalculateur
                .buildDateParticipantMap(participants);
        Assert.assertEquals(4,
                dateParticipantMap.get(DateTime.parse("2013-01-19")).size());
        Assert.assertEquals(5,
                dateParticipantMap.get(DateTime.parse("2013-01-20")).size());
        Assert.assertEquals(2,
                dateParticipantMap.get(DateTime.parse("2013-01-21")).size());
        Map<Depense, Map<Participant, Double>> depenseParticipantsMap = syntheseCalculateur
                .extractionParticipantParDepense(depenses, participants,
                        dateParticipantMap);
        Assert.assertEquals(4, depenseParticipantsMap.get(depenseLudo).size());
        Assert.assertEquals(4, depenseParticipantsMap.get(depenseLolo).size());
        Assert.assertEquals(4, depenseParticipantsMap.get(depenseBenji).size());
        Assert.assertEquals(3, depenseParticipantsMap.get(depenseCharli).size());
        Map<Participant, Map<Participant, Double>> dette = syntheseCalculateur
                .extractionDetteParParticipant(depenseParticipantsMap,
                        participants);

        Map<Participant, Map<Participant, Double>> resultats = syntheseCalculateur
                .gestionEmprunt(dette, emprunts, participants);
        Assert.assertNotNull(resultats);
        Assert.assertEquals(5, resultats.size());
        Assert.assertNotNull(resultats.keySet().iterator().next());
        Double montantAVerifier = resultats.get(ludo).get(benji);
        Assert.assertEquals(Double.valueOf(524.0), montantAVerifier);
    }


}
