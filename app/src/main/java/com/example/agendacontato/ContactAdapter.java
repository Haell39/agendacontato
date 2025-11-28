package com.example.agendacontato;

import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.VH> {

    public interface OnItemClick {
        void onClick(Contact contact);
    }

    public interface OnItemLongClick {
        void onLongClick(Contact contact);
    }

    private List<Contact> contacts;
    private final OnItemClick clickListener;
    private final OnItemLongClick longClickListener;

    public ContactAdapter(List<Contact> contacts, OnItemClick clickListener, OnItemLongClick longClickListener) {
        this.contacts = contacts;
        this.clickListener = clickListener;
        this.longClickListener = longClickListener;
    }

    public void updateContacts(List<Contact> newContacts) {
        this.contacts = newContacts;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Contact c = contacts.get(position);
        
        // Set name
        holder.tvName.setText(c.getName());
        
        // Set phone
        String phone = c.getPhone();
        if (phone != null && !phone.isEmpty()) {
            holder.tvPhone.setText(PhoneFormatter.format(phone));
        } else {
            holder.tvPhone.setText("Sem telefone");
        }
        
        // Set avatar with initials and color
        String initials = AvatarHelper.getInitials(c.getName());
        int color = AvatarHelper.getColorForName(c.getName());
        holder.tvAvatar.setText(initials);
        
        // Create circular background with color
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.OVAL);
        drawable.setColor(color);
        holder.tvAvatar.setBackground(drawable);
        
        // Click handlers
        holder.itemView.setOnClickListener(v -> clickListener.onClick(c));
        holder.itemView.setOnLongClickListener(v -> {
            longClickListener.onLongClick(c);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return contacts != null ? contacts.size() : 0;
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvName, tvPhone, tvAvatar;
        VH(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.item_name);
            tvPhone = itemView.findViewById(R.id.item_phone);
            tvAvatar = itemView.findViewById(R.id.item_avatar);
        }
    }
}
