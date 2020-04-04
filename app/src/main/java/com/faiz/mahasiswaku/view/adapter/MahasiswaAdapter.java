package com.faiz.mahasiswaku.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.faiz.mahasiswaku.R;
import com.faiz.mahasiswaku.model.Mahasiswa;

import java.util.ArrayList;

public class MahasiswaAdapter extends RecyclerView.Adapter<MahasiswaAdapter.ViewHolder> {

    private ArrayList<Mahasiswa> listMahasiswa;
    private OnClickListener onClickListener;

    public MahasiswaAdapter() {
        listMahasiswa = new ArrayList<>();
    }

    public void setListMahasiswa(ArrayList<Mahasiswa> listMahasiswa) {
        this.listMahasiswa = listMahasiswa;
        notifyDataSetChanged();
    }

    public void clearListMahasiswa() {
        listMahasiswa.clear();
        notifyDataSetChanged();
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mahasiswa, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Mahasiswa mahasiswa = listMahasiswa.get(position);
        holder.tvNRP.setText(String.valueOf(mahasiswa.getNrp()));
        holder.tvNama.setText(mahasiswa.getNama());
        holder.tvAlamat.setText(mahasiswa.getAlamat());
        holder.itemView.setOnClickListener(v -> onClickListener.onClick(mahasiswa));
    }

    @Override
    public int getItemCount() {
        return listMahasiswa.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNRP;
        TextView tvNama;
        TextView tvAlamat;
        private ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNRP = itemView.findViewById(R.id.tv_nrp);
            tvNama = itemView.findViewById(R.id.tv_nama);
            tvAlamat = itemView.findViewById(R.id.tv_alamat);
        }
    }

    public interface OnClickListener {
        void onClick(Mahasiswa mahasiswa);
    }
}
