package com.example.agendacontato;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddEditContactActivity extends AppCompatActivity {

    private EditText etName, etPhone, etEmail, etAddress, etNotes;
    private Button btnSave, btnCancel;
    private ContactDAO contactDAO;
    private Contact editingContact = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_contact);

        etName = findViewById(R.id.et_name);
        etPhone = findViewById(R.id.et_phone);
        etEmail = findViewById(R.id.et_email);
        etAddress = findViewById(R.id.et_address);
        etNotes = findViewById(R.id.et_notes);
        btnSave = findViewById(R.id.btn_save);
        btnCancel = findViewById(R.id.btn_cancel);

        contactDAO = new ContactDAO(this);
        contactDAO.open();

        // Checar se veio contact por Intent
        Intent i = getIntent();
        if (i != null && i.hasExtra("contact")) {
            Object obj = i.getSerializableExtra("contact");
            if (obj instanceof Contact) {
                editingContact = (Contact) obj;
                populateForm(editingContact);
            }
        } else if (i != null && i.hasExtra("contact_id")) {
            long id = i.getLongExtra("contact_id", 0);
            if (id > 0) {
                editingContact = contactDAO.getById(id);
                if (editingContact != null) populateForm(editingContact);
            }
        }

        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            if (TextUtils.isEmpty(name)) {
                etName.setError("Nome obrigatório");
                return;
            }
            String phone = etPhone.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String address = etAddress.getText().toString().trim();
            String notes = etNotes.getText().toString().trim();

            if (editingContact == null) {
                Contact newContact = new Contact(0, name, phone, email, address, notes);
                long id = contactDAO.insert(newContact);
                if (id > 0) {
                    Toast.makeText(this, "Contato salvo", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(this, "Erro ao salvar", Toast.LENGTH_SHORT).show();
                }
            } else {
                editingContact.setName(name);
                editingContact.setPhone(phone);
                editingContact.setEmail(email);
                editingContact.setAddress(address);
                editingContact.setNotes(notes);
                int rows = contactDAO.update(editingContact);
                if (rows > 0) {
                    Toast.makeText(this, "Contato atualizado", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(this, "Erro ao atualizar", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnCancel.setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });
    }

    private void populateForm(Contact c) {
        etName.setText(c.getName());
        etPhone.setText(c.getPhone());
        etEmail.setText(c.getEmail());
        etAddress.setText(c.getAddress());
        etNotes.setText(c.getNotes());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (contactDAO != null) contactDAO.close();
    }
}
