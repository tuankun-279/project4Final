package tuan.aprotrain.projectpetcare.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import tuan.aprotrain.projectpetcare.R;
import tuan.aprotrain.projectpetcare.entity.User;

public class UserManagerAdapter extends ArrayAdapter {
    private Activity Context;
    List<User> usersList;
    public UserManagerAdapter(@NonNull Context context, int resource) {
        super(context, resource);
    }
    public UserManagerAdapter(Activity Context, List<User> usersList){
        super(Context, R.layout.list_user, usersList);
        this.Context=Context;
        this.usersList = usersList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        LayoutInflater inflater=Context.getLayoutInflater();
        View listItemView=inflater.inflate(R.layout.list_user,null,true);

        TextView email = listItemView.findViewById(R.id.userEmail);
        TextView id = listItemView.findViewById(R.id.userId);
        TextView name = listItemView.findViewById(R.id.userName);
        TextView phone = listItemView.findViewById(R.id.userPhone);
        TextView role = listItemView.findViewById(R.id.userRoll);
        //ImageView img=listItemView.findViewById(R.id.img1);

        User user = usersList.get(position);
        email.setText(user.getEmail());
        id.setText(user.getUserId());
        name.setText(user.getUserName());
        phone.setText(user.getPhoneNo());
        role.setText(user.getUserRole());

        return listItemView;
    }
}
