package mikazuki.android.app.feelingmatch.view.adapter;

import android.content.Context;
import android.support.annotation.ColorInt;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import mikazuki.android.app.feelingmatch.R;
import mikazuki.android.app.feelingmatch.model.User;

/**
 * @author haijimakazuki
 */
public class SimpleUserListAdapter extends BaseAdapter {

    Context context;
    LayoutInflater layoutInflater = null;
    List<User> memberList;

    @ColorInt
    private int mBoyColorInt;
    @ColorInt
    private int mGirlColorInt;

    public SimpleUserListAdapter(Context context, List<User> memberList) {
        this.context = context;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.memberList = memberList;

        mBoyColorInt = context.getResources().getColor(R.color.boyDark);
        mGirlColorInt = context.getResources().getColor(R.color.girlDark);
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
            convertView = layoutInflater.inflate(android.R.layout.simple_list_item_1, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        User user = memberList.get(position);
        holder.text.setText(user.getName());
        holder.text.setTextColor(user.isBoy() ? mBoyColorInt : mGirlColorInt);
        return convertView;
    }

    public static class ViewHolder {
        @Bind(android.R.id.text1)
        TextView text;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
