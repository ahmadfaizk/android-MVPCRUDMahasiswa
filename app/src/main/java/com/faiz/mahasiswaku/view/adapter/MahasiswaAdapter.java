package com.faiz.mahasiswaku.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.faiz.mahasiswaku.R;
import com.faiz.mahasiswaku.api.ApiClient;
import com.faiz.mahasiswaku.model.Mahasiswa;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

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
        Glide.with(holder.itemView)
                .load(ApiClient.getImageUrl(mahasiswa.getFoto()))
                .placeholder(R.drawable.ic_user)
                .apply(new RequestOptions().override(60, 60))
                .into(holder.imgFoto);
        holder.itemView.setOnClickListener(v -> onClickListener.onClick(mahasiswa));
    }

    @Override
    public int getItemCount() {
        return listMahasiswa.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_nrp) TextView tvNRP;
        @BindView(R.id.tv_nama) TextView tvNama;
        @BindView(R.id.tv_alamat) TextView tvAlamat;
        @BindView(R.id.img_mahasiswa) CircleImageView imgFoto;
        private ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnClickListener {
        void onClick(Mahasiswa mahasiswa);
    }
}
