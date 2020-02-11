package com.example.registration;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageButton;
import com.example.user.User;
import com.example.healthylife.R;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import static com.example.user.User.getInstance;
import static com.example.registration.RegistrationProcessActivity.nextPage;

/**
 * A simple {@link Fragment} subclass.
 */
public class DateOfBirthFragment extends Fragment implements View.OnClickListener {

    private User newUser;
    private int day, month, year;

    private Date date;
    private SimpleDateFormat dateFormat;
    private String formattedDate = "";

    private DatePicker datePicker;
    private ImageButton imageButtonNextDateOfBirth;

    public DateOfBirthFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_date_of_birth, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        newUser = getInstance();

        datePicker = view.findViewById(R.id.DatePicker);

        imageButtonNextDateOfBirth = view.findViewById(R.id.imageButtonNextDateOfBirth);

        imageButtonNextDateOfBirth.setOnClickListener(this);

        date = Calendar.getInstance().getTime();
        dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
        formattedDate = dateFormat.format(date);

        if(newUser.getDateOfBirth().equals("")) {

            datePicker.init(Integer.valueOf(formattedDate.split("\\-")[2]),
                    Integer.valueOf(formattedDate.split("\\-")[1]) - 1,
                    Integer.valueOf(formattedDate.split("\\-")[0]), null);

        } else {

            datePicker.init(Integer.valueOf(newUser.getDateOfBirth().split("\\.")[2]),
                    Integer.valueOf(newUser.getDateOfBirth().split("\\.")[1]) - 1,
                    Integer.valueOf(newUser.getDateOfBirth().split("\\.")[0]), null);
        }
    }

    @Override
    public void onClick(View view) {

        int id = view.getId();

        if(id == R.id.imageButtonNextDateOfBirth) {

            StringBuilder builder = new StringBuilder();

            day = datePicker.getDayOfMonth();
            month = datePicker.getMonth() + 1;
            year = datePicker.getYear();

            builder.append(day);
            builder.append(".");
            builder.append(month);
            builder.append(".");
            builder.append(year);

            newUser.setDateOfBirth(builder.toString());
            calculateAge();

            nextPage();
        }
    }

    private void calculateAge() {

        int currentYear = Integer.valueOf(formattedDate.split("\\-")[2]);
        int yearOfBirth = Integer.valueOf(newUser.getDateOfBirth().split("\\.")[2]);

        newUser.setAge(currentYear - yearOfBirth);
    }
}
