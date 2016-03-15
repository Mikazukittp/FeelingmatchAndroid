package mikazuki.android.app.feelingmatch.view.adapter;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import mikazuki.android.app.feelingmatch.R;
import mikazuki.android.app.feelingmatch.model.User;

/**
 * @author haijimakazuki
 */
public class MemberListAdapter extends BaseAdapter {

    Context context;
    LayoutInflater layoutInflater = null;
    List<User> memberList;

    public MemberListAdapter(Context context, List<User> memberList) {
        this.context = context;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.memberList = memberList;
    }

    @Override
    public int getCount() {
        return memberList.size();
    }

    @Override
    public Object getItem(int position) {
        return memberList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position,
                        View convertView,
                        ViewGroup parent) {
        ViewHolder holder;
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            convertView = layoutInflater.inflate(R.layout.list_member, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        User user = memberList.get(position);
        holder.name.setText(user.getName());
        final int color = user.isBoy() ? R.color.colorPrimaryDark : R.color.colorAccentDark;
        holder.name.setTextColor(context.getResources().getColor(color));
        holder.remove.setOnClickListener(v ->
                new AlertDialog.Builder(context)
                        .setTitle("メンバー削除")
                        .setMessage(user.getName() + "さんを削除して本当によろしいですか？")
                        .setPositiveButton("はい", ((dialog, which) -> {
                            memberList.remove(position);
                            notifyDataSetChanged();
                        })).setNegativeButton("いいえ", null)
                        .create()
                        .show());
        return convertView;
    }

    public static class ViewHolder {
        @Bind(R.id.name)
        TextView name;
        @Bind(R.id.remove)
        ImageButton remove;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
