package com.example.agendacontato;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ContactDAO contactDAO;
    private RecyclerView recyclerView;
    private ContactAdapter adapter;
    private List<Contact> contacts = new ArrayList<>();

    private static final String KEY_CONTACTS = "contacts_list";
    private static final int ADD_CONTACT_REQUEST = 1;
    private static final int EDIT_CONTACT_REQUEST = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        contactDAO = new ContactDAO(this);
        contactDAO.open();

        adapter = new ContactAdapter(
                contacts,
                contact -> {
                    Intent intent = new Intent(MainActivity.this, AddEditContactActivity.class);
                    intent.putExtra("contact", contact);
                    startActivityForResult(intent, EDIT_CONTACT_REQUEST);
                },
                contact -> {
                    new AlertDialog.Builder(this)
                            .setTitle("Excluir contato")
                            .setMessage("Tem certeza que deseja excluir?")
                            .setPositiveButton("Sim", (dialog, which) -> {
                                // ✔ AQUI ESTAVA O ERRO!
                                // Seu DAO espera long, então chamamos delete(contact.getId())
                                contactDAO.delete(contact.getId());
                                loadContactsFromDb();
                            })
                            .setNegativeButton("Não", null)
                            .show();
                }
        );

        recyclerView.setAdapter(adapter);

        if (savedInstanceState != null) {
            contacts = (ArrayList<Contact>) savedInstanceState.getSerializable(KEY_CONTACTS);
            adapter.updateContacts(contacts);
        } else {
            loadContactsFromDb();
        }

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, AddEditContactActivity.class);
            startActivityForResult(intent, ADD_CONTACT_REQUEST);
        });
    }

    private void loadContactsFromDb() {
        contacts.clear();
        contacts.addAll(contactDAO.getAll());
        adapter.updateContacts(contacts);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ((requestCode == ADD_CONTACT_REQUEST || requestCode == EDIT_CONTACT_REQUEST)
                && resultCode == RESULT_OK) {

            loadContactsFromDb();
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(KEY_CONTACTS, new ArrayList<>(contacts));
    }

    @Override
    protected void onResume() {
        super.onResume();
        contactDAO.open();
    }

    @Override
    protected void onPause() {
        super.onPause();
        contactDAO.close();
    }
}
