package mikazuki.android.app.feelingmatch.view.adapter;

import android.content.Context;
import android.os.Vibrator;
import android.support.v7.app.AlertDialog;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.TextAppearanceSpan;
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
        holder.remove.setOnClickListener(v -> {
            TextAppearanceSpan coloredSpan = new TextAppearanceSpan(context, user.isBoy() ? R.style.Boy : R.style.Girl);
            SpannableStringBuilder ssb = new SpannableStringBuilder();
            ssb.append(user.getName());
            ssb.setSpan(coloredSpan, 0, ssb.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            ssb.append("さんを削除して本当によろしいですか？");

            Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(50);

            new AlertDialog.Builder(context)
                    .setTitle("メンバー削除")
//                        .setMessage(user.getName() + "さんを削除して本当によろしいですか？")
                    .setMessage(ssb)
                    .setPositiveButton("はい", ((dialog, which) -> {
                        memberList.remove(position);
                        notifyDataSetChanged();
                    })).setNegativeButton("いいえ", null)
                    .create()
                    .show();
        });
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
