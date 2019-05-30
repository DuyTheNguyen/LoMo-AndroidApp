package com.example.davenguyen.lomoapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Dave Nguyen on 10-Oct-17.
 * Create apater for cretrieving commnet from the database
 */

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    private List<Comment> mCommentList;
    private Context ct;
    public CommentAdapter(List<Comment> mCommentList, Context ct)
    {
        this.ct = ct;
        this.mCommentList = mCommentList;
    }

    @Override
    public CommentAdapter.CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.comment_single_layout,parent,false);

        return new CommentViewHolder(v);
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder{
        private View mView;

        public TextView commentText;
        public TextView timeText;
        public TextView nameText;
        public CircleImageView avatarView;

        public CommentViewHolder(View v)
        {
            super(v);
            mView = v;

            nameText = mView.findViewById(R.id.csl_name);
            commentText = mView.findViewById(R.id.csl_comment);
            timeText = mView.findViewById(R.id.csl_time);
            avatarView = mView.findViewById(R.id.csl_avatar);
        }
    }

    @Override
    public void onBindViewHolder(CommentViewHolder holder, final int position) {
        Comment comment = mCommentList.get(position);
        holder.nameText.setText(comment.getName());
        holder.commentText.setText(comment.getComment());
        holder.timeText.setText(Utility.getDate(comment.getTime()));
        if(!comment.getAvatar().equals("default"))
            Picasso.with(ct).load(comment.getAvatar()).into(holder.avatarView);
        else
            holder.avatarView.setImageResource(R.drawable.ic_account_circle_blue_grey_400_48dp);
        //TODO: Implement on item click
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("CLICK","you just clicked item number " + position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mCommentList.size();
    }
}
