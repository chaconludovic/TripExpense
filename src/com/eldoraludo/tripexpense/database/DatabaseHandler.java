package com.eldoraludo.tripexpense.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.eldoraludo.tripexpense.entite.Depense;
import com.eldoraludo.tripexpense.entite.Participant;
import com.eldoraludo.tripexpense.entite.Projet;
import com.eldoraludo.tripexpense.entite.TypeDeDepense;
import com.eldoraludo.tripexpense.util.DateHelper;


public class DatabaseHandler extends SQLiteOpenHelper {

	// All Static variables
	// Database Version
	private static final int DATABASE_VERSION = 1;

	// Database Name
	private static final String DATABASE_NAME = "tripExpenseDB";

	// Contacts table name
	private static final String TABLE_PROJET = "projet";
	private static final String TABLE_PARTICIPANT = "participant";
	private static final String TABLE_DEPENSE = "depense";

	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// Upgrading database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		onCreate(db);
	}

	// Creating Tables
	@Override
	public void onCreate(SQLiteDatabase db) {
		// DROP table
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_DEPENSE);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_PARTICIPANT);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROJET);

		creationTableProjet(db);
		creationTableParticipant(db);
		creationTableDepense(db);

	}

	private void creationTableProjet(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE IF NOT EXISTS "
				+ TABLE_PROJET
				+ "(id INTEGER PRIMARY KEY AUTOINCREMENT, nom TEXT, est_courant INTEGER)");
	}

	private void creationTableParticipant(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE IF NOT EXISTS "
				+ TABLE_PARTICIPANT
				+ "(id INTEGER PRIMARY KEY AUTOINCREMENT, nom_participant TEXT, projet_id INTEGER,date_arrive TEXT,date_depart TEXT ,FOREIGN KEY (projet_id) REFERENCES PROJET (id))");
	}

	private void creationTableDepense(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE IF NOT EXISTS "
				+ TABLE_DEPENSE
				+ " (id INTEGER PRIMARY KEY AUTOINCREMENT,nom_depense TEXT, "
				+ " montant DOUBLE, date_debut TEXT,date_fin TEXT ,type TEXT, "
				+ " participant_id INTEGER, projet_id INTEGER, "
				+ " FOREIGN KEY (participant_id) REFERENCES PARTICIPANT (id), "
				+ " FOREIGN KEY (projet_id) REFERENCES PROJET (id))");
	}

	/**
	 * All CRUD(Create, Read, Update, Delete) Operations
	 */

	// Adding new projet
	public Projet ajouterOuModifierProjet(Projet projet) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("nom", projet.getNom());
		values.put("est_courant", projet.estCourant() ? 1 : 0);
		if (projet.getId() == null) {
			long insertId = db.insert(TABLE_PROJET, null, values);
			projet.definirLId(insertId);
		} else {
			db.update(TABLE_PROJET, values, "id = ?",
					new String[] { String.valueOf(projet.getId()) });
		}
		db.close();
		return projet;
	}

	// Adding new participant
	public Participant ajouterOuModifierParticipant(Participant participant) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("nom_participant", participant.getNom());
		values.put("projet_id", participant.getProjetId());
		values.put("date_arrive",
				DateHelper.convertirDateToString(participant.getDateArrive()));
		values.put("date_depart",
				DateHelper.convertirDateToString(participant.getDateDepart()));
		if (participant.getId() == null) {
			long insertId = db.insert(TABLE_PARTICIPANT, null, values);
			participant.definirLId(insertId);
		} else {
			db.update(TABLE_PARTICIPANT, values, "id = ?",
					new String[] { String.valueOf(participant.getId()) });
		}
		db.close();
		return participant;
	}

	// Adding new depense
	public Depense ajouterOuModifierDepense(Depense depense) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("nom_depense", depense.getNomDepense());
		values.put("montant", depense.getMontant());
		values.put("type", depense.getTypeDeDepense().name());
		values.put("date_debut",
				DateHelper.convertirDateToString(depense.getDateDebut()));
		values.put("date_fin",
				DateHelper.convertirDateToString(depense.getDateFin()));
		values.put("projet_id", String.valueOf(depense.getProjetId()));
		values.put("participant_id", String.valueOf(depense.getParticipantId()));
		if (depense.getId() == null) {
			long insertId = db.insert(TABLE_DEPENSE, null, values);
			depense.definirLId(insertId);
		} else {
			db.update(TABLE_DEPENSE, values, "id = ?",
					new String[] { String.valueOf(depense.getId()) });
		}
		db.close();
		return depense;
	}

	// Getting single projet
	public Projet trouverLeProjet(Integer projetId) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_PROJET, new String[] { "id", "nom",
				"est_courant" }, "id=?",
				new String[] { String.valueOf(projetId) }, null, null, null,
				null);
		Projet projet = null;
		if (cursor != null) {
			cursor.moveToFirst();
			projet = Projet
					.newBuilder()
					.withId(cursor.getInt(0))
					.withNom(cursor.getString(1))
					.withEstProjetCourant(
							(cursor.getInt(2) == 0) ? false : true).build();
		}
		db.close();
		return projet;
	}

	public Participant trouverLeParticipant(Integer idParticipant) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_PARTICIPANT, new String[] { "id",
				"nom_participant", "date_arrive", "date_depart", "projet_id" },
				"id=?", new String[] { String.valueOf(idParticipant) }, null,
				null, null, null);
		Participant participant = null;
		if (cursor != null) {
			cursor.moveToFirst();
			participant = Participant
					.newBuilder()
					.withId(cursor.getInt(0))
					.withNom(cursor.getString(1))
					.withDateArrive(
							DateHelper.convertirStringToDate(cursor
									.getString(2)))
					.withDateDepart(
							DateHelper.convertirStringToDate(cursor
									.getString(3)))
					.withProjetId(cursor.getInt(4)).build();
		}
		db.close();
		return participant;
	}

	public Depense trouverLaDepense(Integer idDepense) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_DEPENSE, new String[] { "id",
				"nom_depense", " montant", "date_debut", "date_fin", "type",
				"participant_id", "projet_id" }, "id=?",
				new String[] { String.valueOf(idDepense) }, null, null, null,
				null);
		Depense depense = null;
		if (cursor != null) {
			cursor.moveToFirst();
			depense = Depense
					.newBuilder()
					.withId(cursor.getInt(0))
					.withNomDepense(cursor.getString(1))
					.withMontant(Double.valueOf(cursor.getString(2)))
					.withDateDebut(
							DateHelper.convertirStringToDate(cursor
									.getString(3)))
					.withDateFin(
							DateHelper.convertirStringToDate(cursor
									.getString(4)))
					.withTypeDeDepense(
							TypeDeDepense.valueOf(cursor.getString(5)))
					.withParticipantId(cursor.getInt(6))
					.withProjetId(cursor.getInt(7)).build();
		}
		db.close();
		return depense;
	}

	// Getting single projet
	public Projet trouverLeProjetCourant() {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_PROJET, new String[] { "id", "nom",
				"est_courant" }, "est_courant=1", new String[] {}, null, null,
				null, null);
		if (cursor != null)
			cursor.moveToFirst();

		Projet projet = Projet.newBuilder().withId(cursor.getInt(0))
				.withNom(cursor.getString(1))
				.withEstProjetCourant((cursor.getInt(2) == 0) ? false : true)
				.build();
		db.close();
		return projet;
	}

	// Deleting single projet
	public void deleteProjet(Projet projet) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_PROJET, "id = ?",
				new String[] { String.valueOf(projet.getId()) });
		db.close();
	}

	// Getting projets Count
	public int getProjetsCount() {
		String countQuery = "SELECT  * FROM " + TABLE_PROJET;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		int count = cursor.getCount();
		cursor.close();
		return count;
	}

	public List<Participant> getAllParticipant(Integer projetId) {
		List<Participant> participantList = new ArrayList<Participant>();
		// String selectQuery = "SELECT  * FROM " + TABLE_PARTICIPANT;
		SQLiteDatabase db = this.getReadableDatabase();
		// Cursor cursor = db.rawQuery(selectQuery, null);

		Cursor cursor = db.query(TABLE_PARTICIPANT, new String[] { "id",
				"nom_participant", "date_arrive", "date_depart", "projet_id" },
				"projet_id=?", new String[] { String.valueOf(projetId) }, null,
				null, null, null);

		if (cursor.moveToFirst()) {
			do {
				participantList.add(Participant
						.newBuilder()
						.withId(cursor.getInt(0))
						.withNom(cursor.getString(1))
						.withDateArrive(
								DateHelper.convertirStringToDate(cursor
										.getString(2)))
						.withDateDepart(
								DateHelper.convertirStringToDate(cursor
										.getString(3)))
						.withProjetId(cursor.getInt(4)).build());
			} while (cursor.moveToNext());
		}
		db.close();
		return participantList;
	}

	public List<Depense> getAllDepense(Integer projetId) {
		List<Depense> depenseList = new ArrayList<Depense>();
		// String selectQuery = "SELECT  * FROM " + TABLE_PARTICIPANT;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_DEPENSE, new String[] { "id",
				"nom_depense", " montant", "date_debut", "date_fin", "type",
				"participant_id", "projet_id" }, "projet_id=?",
				new String[] { String.valueOf(projetId) }, null, null, null,
				null);

		if (cursor.moveToFirst()) {
			do {
				depenseList.add(Depense
						.newBuilder()
						.withId(cursor.getInt(0))
						.withNomDepense(cursor.getString(1))
						.withMontant(Double.valueOf(cursor.getString(2)))
						.withDateDebut(
								DateHelper.convertirStringToDate(cursor
										.getString(3)))
						.withDateFin(
								DateHelper.convertirStringToDate(cursor
										.getString(4)))
						.withTypeDeDepense(
								TypeDeDepense.valueOf(cursor.getString(5)))
						.withParticipantId(cursor.getInt(6))
						.withProjetId(cursor.getInt(7)).build());
			} while (cursor.moveToNext());
		}
		db.close();
		return depenseList;
	}

	// Getting All projets
	public List<Projet> getAllProjet() {
		List<Projet> projetList = new ArrayList<Projet>();
		String selectQuery = "SELECT  * FROM " + TABLE_PROJET;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				projetList
						.add(Projet
								.newBuilder()
								.withId(cursor.getInt(0))
								.withNom(cursor.getString(1))
								.withEstProjetCourant(
										(cursor.getInt(2) == 0) ? false : true)
								.build());
			} while (cursor.moveToNext());
		}
		db.close();
		return projetList;
	}

}
