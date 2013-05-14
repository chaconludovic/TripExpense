package com.eldoraludo.tripexpense;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.joda.time.DateTime;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;

import com.eldoraludo.tripexpense.database.DatabaseHandler;
import com.eldoraludo.tripexpense.entite.Depense;
import com.eldoraludo.tripexpense.entite.Participant;

public class SyntheseActivity extends ListActivity {
	private Integer idProjet;
	private DatabaseHandler databaseHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_synthese);

		Intent intent = getIntent();
		idProjet = intent.getIntExtra(GestionProjetActivity.ID_PROJET_COURANT,
				0);
		databaseHandler = new DatabaseHandler(this);

		this.setListAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, getListeDettes()));

		// Show the Up button in the action bar.
		setupActionBar();
	}

	private List<String> getListeDettes() {
		List<Depense> depenses = databaseHandler.getAllDepense(idProjet);
		List<Participant> participants = databaseHandler
				.getAllParticipant(idProjet);

		Map<DateTime, Set<Participant>> dateParticipantMap = this
				.buildDateParticipantMap(participants);

		Map<Depense, Map<Participant, Double>> depenseParticipantsMap = extractionParticipantParDepense(
				depenses, participants, dateParticipantMap);

		Map<Participant, Map<Participant, Double>> dette = extractionDetteParParticipant(depenseParticipantsMap);

		return extractionListeDette(dette);

	}

	private List<String> extractionListeDette(
			Map<Participant, Map<Participant, Double>> dette) {
		List<String> res = new ArrayList<String>();
		for (Participant participant : dette.keySet()) {
			for (Participant depenseur : dette.get(participant).keySet()) {
				String ress = participant.getNom() + " doit à "
						+ depenseur.getNom() + " la somme de "
						+ dette.get(participant).get(depenseur);
				res.add(ress);
			}
		}
		return res;
	}

	private Map<Participant, Map<Participant, Double>> extractionDetteParParticipant(
			Map<Depense, Map<Participant, Double>> depenseParticipantsMap) {
		Map<Participant, Map<Participant, Double>> dette = new HashMap<Participant, Map<Participant, Double>>();
		for (Depense depense : depenseParticipantsMap.keySet()) {
			Participant depenseur = databaseHandler
					.trouverLeParticipant(depense.getParticipantId());
			Map<Participant, Double> map = depenseParticipantsMap.get(depense);
			for (Participant participant : map.keySet()) {
				if (!dette.containsKey(participant)) {
					dette.put(participant, new HashMap<Participant, Double>());
				}
				if (!dette.get(participant).containsKey(depenseur)) {
					dette.get(participant).put(depenseur, 0.0);
				}
				dette.get(participant).put(
						depenseur,
						dette.get(participant).get(depenseur)
								+ map.get(participant));
			}
		}
		// nettoyage
		Map<Participant, Map<Participant, Double>> detteFinal = copieDeLaMap(dette);
		for (Participant participant : dette.keySet()) {
			if (!detteFinal.containsKey(participant)) {
				continue;
			}
			for (Participant depenseur : dette.get(participant).keySet()) {
				if (!detteFinal.containsKey(depenseur)) {
					continue;
				}
				if (dette.containsKey(depenseur)
						&& dette.get(depenseur).containsKey(participant)) {
					Double montantDuParLeDepenseurAuParticipant = dette.get(
							depenseur).get(participant);
					if (this.estDetteDuDepenseSuperieurADetteDuParticipant(
							dette, participant, depenseur,
							montantDuParLeDepenseurAuParticipant)) {
						this.recalculDeLaDetteDuDepenseur(dette, detteFinal,
								participant, depenseur,
								montantDuParLeDepenseurAuParticipant);
					} else {
						this.recalculDeLaDetteDuParticipant(dette, detteFinal,
								participant, depenseur,
								montantDuParLeDepenseurAuParticipant);
					}
				}
			}
		}
		return detteFinal;
	}

	private Map<Participant, Map<Participant, Double>> copieDeLaMap(
			Map<Participant, Map<Participant, Double>> dette) {
		Map<Participant, Map<Participant, Double>> detteFinal = new HashMap<Participant, Map<Participant, Double>>();
		for (Participant participant : dette.keySet()) {
			Map<Participant, Double> mapDepenseur = new HashMap<Participant, Double>();
			for (Participant depenseur : dette.get(participant).keySet()) {
				mapDepenseur.put(depenseur,
						dette.get(participant).get(depenseur));
			}
			detteFinal.put(participant, mapDepenseur);
		}
		return detteFinal;
	}

	private void recalculDeLaDetteDuParticipant(
			Map<Participant, Map<Participant, Double>> dette,
			Map<Participant, Map<Participant, Double>> detteFinal,
			Participant participant, Participant depenseur,
			Double detteDuDepenseur) {
		Double detteDuParticipant = dette.get(participant).get(depenseur);
		Double detteRecalcule = detteDuParticipant - detteDuDepenseur;
		detteFinal.get(participant).put(depenseur, detteRecalcule);
		detteFinal.get(depenseur).remove(participant);
		if (detteFinal.get(depenseur).size() == 0) {
			detteFinal.remove(depenseur);
		}
	}

	private void recalculDeLaDetteDuDepenseur(
			Map<Participant, Map<Participant, Double>> dette,
			Map<Participant, Map<Participant, Double>> detteFinal,
			Participant participant, Participant depenseur,
			Double detteDuDepenseur) {
		Double detteDuParticipant = dette.get(participant).get(depenseur);
		Double detteRecalcule = detteDuDepenseur - detteDuParticipant;
		detteFinal.get(depenseur).put(participant, detteRecalcule);
		detteFinal.get(participant).remove(depenseur);
		if (detteFinal.get(participant).size() == 0) {
			detteFinal.remove(participant);
		}
	}

	private boolean estDetteDuDepenseSuperieurADetteDuParticipant(
			Map<Participant, Map<Participant, Double>> dette,
			Participant participant, Participant depenseur,
			Double montantDuParLeDepenseurAuParticipant) {
		return montantDuParLeDepenseurAuParticipant > dette.get(participant)
				.get(depenseur);
	}

	private Map<Depense, Map<Participant, Double>> extractionParticipantParDepense(
			List<Depense> depenses, List<Participant> participants,
			Map<DateTime, Set<Participant>> dateParticipantMap) {
		Map<Depense, Map<Participant, Double>> depenseParticipantsMap = new HashMap<Depense, Map<Participant, Double>>();
		for (Depense depense : depenses) {
			DateTime dateDebut = depense.getDateDebut();
			DateTime dateFin = depense.getDateFin();
			DateTime dateCourante = dateDebut;
			int nombreDeParticipantPourLaDepense = 0;
			while (!dateCourante.isAfter(dateFin)) {
				Set<Participant> participantsDuJour = dateParticipantMap
						.get(dateCourante);
				if (participantsDuJour != null) {
					nombreDeParticipantPourLaDepense += participantsDuJour
							.size();
				}
				dateCourante = dateCourante.plusDays(1);
			}

			Map<Participant, Double> mapPart = new HashMap<Participant, Double>();
			Double montantParPart = depense.getMontant()
					/ Double.valueOf(nombreDeParticipantPourLaDepense);
			for (Participant participant : participants) {
				if (participant.getId().equals(depense.getParticipantId())) {
					continue;
				}
				dateCourante = dateDebut;
				while (!dateCourante.isAfter(dateFin)) {
					Set<Participant> participantsDuJour = dateParticipantMap
							.get(dateCourante);
					if (participantsDuJour != null
							&& participantsDuJour.contains(participant)) {
						if (!mapPart.containsKey(participant)) {
							mapPart.put(participant, 0.0);
						}
						mapPart.put(participant, mapPart.get(participant)
								+ montantParPart);
					}
					dateCourante = dateCourante.plusDays(1);
				}
			}
			depenseParticipantsMap.put(depense, mapPart);
		}
		return depenseParticipantsMap;
	}

	public Map<DateTime, Set<Participant>> buildDateParticipantMap(
			List<Participant> participants) {
		Map<DateTime, Set<Participant>> map = new TreeMap<DateTime, Set<Participant>>();
		DateTime dateCourante = null;
		for (Participant participant : participants) {
			dateCourante = participant.getDateArrive();
			while (!dateCourante.isAfter(participant.getDateDepart())) {
				if (map.isEmpty()) {
					Set<Participant> list = new HashSet<Participant>();
					list.add(participant);
					map.put(dateCourante, list);
				} else {
					if (!map.containsKey(dateCourante)) {
						Set<Participant> list = new HashSet<Participant>();
						map.put(dateCourante, list);
					}
					map.get(dateCourante).add(participant);
				}
				dateCourante = dateCourante.plusDays(1);
			}
		}
		return map;
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {
		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.synthese, menu);
		if (databaseHandler.getParticipantsCount(idProjet) == 0) {
			MenuItem item = menu.findItem(R.id.gestion_depense_menu);
			item.setVisible(false);
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.gestion_projet_menu:
			startActivity(new Intent(getApplicationContext(),
					GestionProjetActivity.class));
			return true;
		case R.id.gestion_participant_menu:
			Intent i = new Intent(getApplicationContext(),
					GestionParticipantActivity.class);
			i.putExtra(GestionProjetActivity.ID_PROJET_COURANT, idProjet);
			startActivity(i);
			return true;
		case R.id.gestion_depense_menu:
			Intent pageDepense = new Intent(getApplicationContext(),
					GestionDepenseActivity.class);
			pageDepense.putExtra(GestionProjetActivity.ID_PROJET_COURANT,
					idProjet);
			startActivity(pageDepense);
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

}
