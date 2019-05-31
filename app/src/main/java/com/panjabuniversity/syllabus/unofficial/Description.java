package com.panjabuniversity.syllabus.unofficial;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class Description extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description);
        TextView mTextView = findViewById(R.id.textView);

        String mString = "Panjab University (PU) is a State University " +
                "which is widely known as Punjab University (PU), located in" + "" +
                "the capital city of Chandigarh in Punjab state of India. PU " + "" +
                "was established in the year 1882 and is approved by The All India " + "" +
                "Council of Technical Education (AICTE) and accredited by the National " + "" +
                "Assessment and Accreditation Council (NAAC). PU is also recognized by " + "" +
                "The University Grants Commission (UGC) and associated with the Association" + "" +
                "of Indian Universities (AIU). \n\nThe university offers various UG, Integrated, " + "" +
                "PG, Executive, Diploma, PG Diploma, Certificate, and Research Level courses." + "" +
                "Admission to certain courses is based on Entrance Exams conducted by the University." + "" +
                "\n\n" +
                "The University's chequerboard layout, devised by Swiss French Architect Pierre Jeanneret, was a role model for campus design in India.\n" +
                "\n" +
                "The main campus at Chandigarh is spread over 550 acres in sectors 14 and 25, the teaching area is in the north-east, with the Central Library, Fine Arts Museum, and three-winged structure of the Gandhi Bhawan forming its core; the sports complex, the health centre, student centre and the shopping centre in the middle; 16 university hostel and residential area in the south-east, stretching into the adjacent sector 25 which also houses the University Institute of Engineering and Technology and Dr. Harvansh Singh Judge Institute of dental Sciences and Hospital, UIAMS, Institute of Biological sciences etc.\n" +
                "\n" +
                "The campus has amenities like a State bank of India branch, Post and Sampark, public transport system, open-air theatre, guest and faculty houses, seminar complexes, staff club, several spacious lawns, botanical and medicinal herbs gardens, a newly laid rose garden, a school and a day-care centre for the employees' children. The campus is adjacent to a medical institution known as the Post Graduate Institute of Medical Education and Research";

        mTextView.setText(mString);
    }

    public void click(View view) {
        finish();
    }


}