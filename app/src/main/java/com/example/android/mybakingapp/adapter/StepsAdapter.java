package com.example.android.mybakingapp.adapter;

import android.app.Activity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.mybakingapp.R;
import com.example.android.mybakingapp.model.Step;
import com.squareup.picasso.Picasso;

import java.lang.ref.WeakReference;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Adapter that shows all the steps from a Recipe. Displays id and short description
 */
public class StepsAdapter extends RecyclerView.Adapter<StepsAdapter.ViewHolder> {

    private List<Step> mSteps;
    private WeakReference<Activity> mContext;
    private StepClickListener mStepClickListener;

    public StepsAdapter(Activity mContext, List<Step> steps) {
        this.mContext = new WeakReference<>(mContext);
        setModels(steps);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.card_view)
        CardView cardViewMain;
        @BindView(R.id.recipe_component_img)
        ImageView imgComponentImg;
        @BindView(R.id.recipe_component_name)
        TextView txtComponentName;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.card_view)
        public void onClickComponent() {
            if (mStepClickListener != null) {
                mStepClickListener.onStepSelected(getAdapterPosition());
            }
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_component_row, parent, false);
        ViewHolder vhItem = new ViewHolder(v);
        return vhItem;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        Step step = mSteps.get(position);
        if (position != 0) {
            viewHolder.txtComponentName.setText((step.getId()) + ". " + step.getShortDescription());
        } else {
            viewHolder.txtComponentName.setText(step.getShortDescription());
        }
        if (!TextUtils.isEmpty(step.getThumbnailURL())) {
            Picasso.with(mContext.get())
                    .load(step.getThumbnailURL())
                    .placeholder(R.drawable.ic_local_dining_black_48dp)
                    .error(R.drawable.ic_local_dining_black_48dp)
                    .into(viewHolder.imgComponentImg);
        }
    }

    @Override
    public int getItemCount() {
        if (mSteps != null) {
            return mSteps.size();
        } else {
            return 0;
        }
    }

    public void setModels(List<Step> steps) {
        mSteps = steps;
        notifyDataSetChanged();
    }

    public interface StepClickListener {
        void onStepSelected(int position);
    }

    public void setStepClickListener(StepClickListener mStepClickListener) {
        this.mStepClickListener = mStepClickListener;
    }
}