package com.eldoraludo.tripexpense;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.eldoraludo.tripexpense.database.DatabaseHandler;
import com.eldoraludo.tripexpense.entite.Projet;

public class FicheProjetActivity extends Activity {
    private DatabaseHandler databaseHandler;
    private static final int REQUEST_MODIFIER_PROJET = 1;

    private Integer idProjet;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fiche_projet);
        Intent intent = getIntent();
        idProjet = intent.getIntExtra(GestionProjetActivity.ID_PROJET_COURANT,
                -1);
        databaseHandler = new DatabaseHandler(this);
        initialiserLaFiche();
    }

    private void initialiserLaFiche() {
        if (idProjet != -1) {
            Projet projet = databaseHandler.trouverLeProjet(idProjet);
            TextView nomDuProjetText = (TextView) this
                    .findViewById(R.id.nomDuProjet);
            nomDuProjetText.setText(projet.getNom());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.fiche_projet, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.fiche_projet_ajouter_participant_menu:
                Intent i = new Intent(getApplicationContext(),
                        GestionParticipantActivity.class);
                i.putExtra(GestionProjetActivity.ID_PROJET_COURANT, idProjet);
                startActivity(i);
                return true;
            case R.id.fiche_projet_modifier_projet_menu:
                Intent pageAjouterProjet = new Intent(getApplicationContext(),
                        AjouterProjetActivity.class);
                pageAjouterProjet.putExtra(GestionProjetActivity.ID_PROJET_COURANT,
                        idProjet);
                startActivityForResult(pageAjouterProjet, REQUEST_MODIFIER_PROJET);
                return true;
            case R.id.fiche_projet_synthese_menu:
                Intent pageSynthese = new Intent(getApplicationContext(),
                        SyntheseActivity.class);
                pageSynthese.putExtra(GestionProjetActivity.ID_PROJET_COURANT,
                        idProjet);
                startActivity(pageSynthese);
                super.finish();
                return true;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_MODIFIER_PROJET) {
                initialiserLaFiche();
            }
        }
    }
}