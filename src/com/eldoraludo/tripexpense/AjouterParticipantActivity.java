package com.eldoraludo.tripexpense;

import java.util.Calendar;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.eldoraludo.tripexpense.database.DatabaseHandler;
import com.eldoraludo.tripexpense.entite.Participant;
import com.eldoraludo.tripexpense.util.DateHelper;
import com.google.common.base.Preconditions;

public class AjouterParticipantActivity extends Activity {
    private static final int CONTACT_PICKER_RESULT = 0;
    private Integer idParticipant;
    private Integer idProjet;
    private Button ajouterOuModifierParticipantButton;
    private Button boutonRecupererUnContact;
    private EditText nomParticipantText;
    private TextView tvDisplayDateArrive;
    private Button btnChangeDateArrive;
    private TextView tvDisplayDateDepart;
    private Button btnChangeDateDepart;
    private String nomRecupererDepuisListeContact;
    private String idContactRecupereDepuisListeContact;

    private int anneeArrive;
    private int moisArrive;
    private int jourArrive;
    private int anneeDepart;
    private int moisDepart;
    private int jourDepart;
    private DatabaseHandler databaseHandler;
    static final int DATE_DIALOG_ID_ARRIVE = 999;
    static final int DATE_DIALOG_ID_DEPART = 1999;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajouter_participant);

        Intent intent = getIntent();
        idParticipant = intent.getIntExtra(
                GestionParticipantActivity.ID_PARTICIPANT, -1);
        idProjet = intent.getIntExtra(GestionProjetActivity.ID_PROJET_COURANT,
                -1);
        Preconditions.checkState(!idProjet.equals(-1),
                "L'id du projet doit être définit");
        databaseHandler = new DatabaseHandler(this);
        nomParticipantText = (EditText) findViewById(R.id.nomParticipantText);
        ajouterOuModifierParticipantButton = (Button) findViewById(R.id.ajouterOuModifierParticipantButton);
        boutonRecupererUnContact = (Button) findViewById(R.id.btnGetContact);

        if (idParticipant != -1) {
            Participant participant = databaseHandler
                    .trouverLeParticipant(idParticipant);
            Preconditions.checkNotNull(participant,
                    "Le participant n'a pas été trouvé");
            nomParticipantText.setText(participant.getNom());
            anneeArrive = participant.getDateArrive().getYear();
            moisArrive = participant.getDateArrive().getMonthOfYear();
            jourArrive = participant.getDateArrive().getDayOfMonth();

            anneeDepart = participant.getDateDepart().getYear();
            moisDepart = participant.getDateDepart().getMonthOfYear();
            jourDepart = participant.getDateDepart().getDayOfMonth();

            if (participant.getContactPhoneId() != null) {
                idContactRecupereDepuisListeContact = participant.getContactPhoneId();
                nomRecupererDepuisListeContact = participant.getNom();
            }
        } else {
            final Calendar c = Calendar.getInstance();
            anneeArrive = c.get(Calendar.YEAR);
            moisArrive = c.get(Calendar.MONTH);
            jourArrive = c.get(Calendar.DAY_OF_MONTH);
            anneeDepart = c.get(Calendar.YEAR);
            moisDepart = c.get(Calendar.MONTH);
            jourDepart = c.get(Calendar.DAY_OF_MONTH) + 1;
        }

        setCurrentDateOnView();
        addListenerOnButton();

    }

    public void onClick(View view) {
        // If add button was clicked
        if (ajouterOuModifierParticipantButton.isPressed()) {
            // Get entered text
            String participantTextValue = nomParticipantText.getText()
                    .toString();
            if (participantTextValue == null || participantTextValue.isEmpty()) {
                return;
            }
            nomParticipantText.setText("");
            // Add text to the database
            Participant.Builder participantBuilder = Participant
                    .newBuilder()
                    .withId(idParticipant == -1 ? null : idParticipant)
                    .withNom(participantTextValue)
                    .withDateArrive(
                            DateHelper.convertirIntsToDate(jourArrive,
                                    moisArrive, anneeArrive))
                    .withDateDepart(
                            DateHelper.convertirIntsToDate(jourDepart,
                                    moisDepart, anneeDepart))
                    .withProjetId(idProjet);
            if (participantTextValue.equals(nomRecupererDepuisListeContact)) {
                participantBuilder.withContactPhoneId(idContactRecupereDepuisListeContact);
            } else {
                participantBuilder.withContactPhoneId(null);
            }
            databaseHandler.ajouterOuModifierParticipant(participantBuilder.build());
            Intent i = new Intent();
            setResult(RESULT_OK, i);
            super.finish();
        } else if (boutonRecupererUnContact.isPressed()) {
            Intent contactPickerIntent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
            startActivityForResult(contactPickerIntent, CONTACT_PICKER_RESULT);
        }

    }

    // display current date
    public void setCurrentDateOnView() {
        tvDisplayDateArrive = (TextView) findViewById(R.id.afficherDateArrive);
        tvDisplayDateDepart = (TextView) findViewById(R.id.afficherDateDepart);
        // set current date into textview
        tvDisplayDateArrive.setText(new StringBuilder()
                // Month is 0 based, just add 1
                .append(jourArrive).append("/").append(moisArrive).append("/")
                .append(anneeArrive).append(" "));
        tvDisplayDateDepart.setText(new StringBuilder()
                // Month is 0 based, just add 1
                .append(jourDepart).append("/").append(moisDepart).append("/")
                .append(anneeDepart).append(" "));
    }

    public void addListenerOnButton() {

        btnChangeDateArrive = (Button) findViewById(R.id.btnChangeDateArrive);

        btnChangeDateArrive.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                showDialog(DATE_DIALOG_ID_ARRIVE);

            }

        });
        btnChangeDateDepart = (Button) findViewById(R.id.btnChangeDateDepart);

        btnChangeDateDepart.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                showDialog(DATE_DIALOG_ID_DEPART);

            }

        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CONTACT_PICKER_RESULT:
                    try {
                        Uri result = data.getData();
                        idContactRecupereDepuisListeContact = result.getLastPathSegment();
                        Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + "=?", new String[]{idContactRecupereDepuisListeContact}, null);
                        if (cursor.moveToFirst()) {
                            nomRecupererDepuisListeContact = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                            nomParticipantText.setText(nomRecupererDepuisListeContact);
                        }
                    } catch (Exception e) {
                        Log.e("", "Failed to get email data", e);
                    } finally {

                    }
                    break;
            }

        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID_ARRIVE:
                // set date picker as current date
                return new DatePickerDialog(this, datePickerListenerArrive,
                        anneeArrive, moisArrive - 1, jourArrive);
            case DATE_DIALOG_ID_DEPART:
                // set date picker as current date
                return new DatePickerDialog(this, datePickerListenerDepart,
                        anneeDepart, moisDepart - 1, jourDepart);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener datePickerListenerArrive = new DatePickerDialog.OnDateSetListener() {

        // when dialog box is closed, below method will be called.
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {
            anneeArrive = selectedYear;
            moisArrive = selectedMonth + 1;
            jourArrive = selectedDay;

            // set selected date into textview
            tvDisplayDateArrive.setText(new StringBuilder().append(jourArrive)
                    .append("/").append(moisArrive).append("/")
                    .append(anneeArrive).append(" "));

        }
    };
    private DatePickerDialog.OnDateSetListener datePickerListenerDepart = new DatePickerDialog.OnDateSetListener() {

        // when dialog box is closed, below method will be called.
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {
            anneeDepart = selectedYear;
            moisDepart = selectedMonth + 1;
            jourDepart = selectedDay;

            // set selected date into textview
            tvDisplayDateDepart.setText(new StringBuilder().append(jourDepart)
                    .append("/").append(moisDepart).append("/")
                    .append(anneeDepart).append(" "));

        }
    };

}
