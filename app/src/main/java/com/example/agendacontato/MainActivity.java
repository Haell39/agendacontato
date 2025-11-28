package com.example.agendacontato;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private ContactDAO contactDAO;
    private RecyclerView recyclerView;
    private ContactAdapter adapter;
    private List<Contact> contacts = new ArrayList<>();
    private List<Contact> filteredContacts = new ArrayList<>();
    private LinearLayout emptyState;
    private MaterialToolbar toolbar;

    private static final String KEY_CONTACTS = "contacts_list";
    private static final int ADD_CONTACT_REQUEST = 1;
    private static final int EDIT_CONTACT_REQUEST = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        emptyState = findViewById(R.id.emptyState);

        contactDAO = new ContactDAO(this);
        contactDAO.open();

        adapter = new ContactAdapter(
                filteredContacts,
                contact -> {
                    Intent intent = new Intent(MainActivity.this, AddEditContactActivity.class);
                    intent.putExtra("contact", contact);
                    startActivityForResult(intent, EDIT_CONTACT_REQUEST);
                },
                contact -> {
                    new AlertDialog.Builder(this)
                            .setTitle("Excluir contato")
                            .setMessage("Tem certeza que deseja excluir " + contact.getName() + "?")
                            .setPositiveButton("Sim", (dialog, which) -> {
                                int deleted = contactDAO.delete(contact.getId());
                                if (deleted > 0) {
                                    loadContactsFromDb();
                                    Snackbar.make(recyclerView, "Contato excluído", Snackbar.LENGTH_SHORT).show();
                                } else {
                                    Snackbar.make(recyclerView, "Erro ao excluir", Snackbar.LENGTH_SHORT).show();
                                }
                            })
                            .setNegativeButton("Não", null)
                            .show();
                }
        );

        recyclerView.setAdapter(adapter);

        if (savedInstanceState != null) {
            contacts = (ArrayList<Contact>) savedInstanceState.getSerializable(KEY_CONTACTS);
            filteredContacts.clear();
            filteredContacts.addAll(contacts);
            adapter.updateContacts(filteredContacts);
        } else {
            loadContactsFromDb();
        }
        updateEmptyState();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, AddEditContactActivity.class);
            startActivityForResult(intent, ADD_CONTACT_REQUEST);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        
        // Setup search
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setQueryHint("Buscar contato...");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterContacts(newText);
                return true;
            }
        });
        
        return true;
    }

    private void filterContacts(String query) {
        filteredContacts.clear();
        
        if (query == null || query.trim().isEmpty()) {
            filteredContacts.addAll(contacts);
        } else {
            String lowerQuery = query.toLowerCase(Locale.getDefault());
            for (Contact contact : contacts) {
                if (contact.getName().toLowerCase(Locale.getDefault()).contains(lowerQuery) ||
                    (contact.getPhone() != null && contact.getPhone().contains(lowerQuery)) ||
                    (contact.getEmail() != null && contact.getEmail().toLowerCase(Locale.getDefault()).contains(lowerQuery))) {
                    filteredContacts.add(contact);
                }
            }
        }
        
        adapter.updateContacts(filteredContacts);
        updateEmptyState();
    }

    private void loadContactsFromDb() {
        contacts.clear();
        contacts.addAll(contactDAO.getAll());
        filteredContacts.clear();
        filteredContacts.addAll(contacts);
        adapter.updateContacts(filteredContacts);
        updateEmptyState();
    }

    private void updateEmptyState() {
        if (filteredContacts.isEmpty()) {
            emptyState.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyState.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
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
