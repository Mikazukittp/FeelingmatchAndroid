package mikazuki.android.app.feelingmatch.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import mikazuki.android.app.feelingmatch.R;
import mikazuki.android.app.feelingmatch.model.User;

/**
 * @author haijimakazuki
 */
public class CandidateListAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater layoutInflater = null;
    private List<AppData> memberList;

    public CandidateListAdapter(Context context, List<User> memberList) {
        this.context = context;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.memberList = Stream.of(memberList).map(AppData::new).collect(Collectors.toList());
    }

    @Override
    public int getCount() {
        return memberList.size();
    }

    @Override
    public AppData getItem(int position) {
        return memberList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public User getSelectedUser() {
        return Stream.of(memberList).filter(AppData::isChecked).findFirst().orElse(new AppData(null)).getUser();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            convertView = layoutInflater.inflate(R.layout.list_candidate, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }

        final AppData rowData = memberList.get(position);

        holder.heart.setImageResource(rowData.isChecked() ? R.drawable.ic_favorite_red_800_36dp : R.drawable.ic_favorite_border_grey_400_36dp);
        holder.name.setText(rowData.getUser().getName());
        final int color = rowData.getUser().isBoy() ? R.color.colorPrimaryDark : R.color.colorAccentDark;
        holder.name.setTextColor(context.getResources().getColor(color));
        return convertView;
    }

    public static class ViewHolder {
        @Bind(R.id.heart)
        ImageView heart;
        @Bind(R.id.name)
        TextView name;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public class AppData {
        private User user;
        private boolean isChecked;

        public AppData(User user) {
            this(user, false);
        }

        public AppData(User user, boolean isChecked) {
            this.user = user;
            this.isChecked = isChecked;
        }

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }

        public boolean isChecked() {
            return isChecked;
        }

        public void setChecked(boolean checked) {
            isChecked = checked;
        }
    }

}
