package com.bbits.park;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class AddImageActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST = 1888;

    GridView imagesGrid;
    ArrayList<Bitmap> bitmapArray = new ArrayList<Bitmap>();
    ImageView addImageBtn;
    GlobalContext data= new GlobalContext();
    Button nextBtn;

    TextView headerRight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_image);

//        drawable = ContextCompat.getDrawable(AddImageActivity.this,R.drawable.addimageicon);

        nextBtn = findViewById(R.id.nextBtn);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bitmapArray.size() != 0){
                    data.setSelectedImages(bitmapArray);

                    finish();
                }
                else{
                    Toast.makeText(AddImageActivity.this, "Please select atleast 1 image!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        imagesGrid = findViewById(R.id.imagesGrid);
        headerRight = findViewById(R.id.headerRight);
        headerRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

//        imagesGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
////                Toast.makeText(AddImageActivity.this, String.valueOf(i), Toast.LENGTH_SHORT).show();
//                bitmapArray.remove(i);
//                imagesGrid.setAdapter(null);
//                imagesGrid.setAdapter(new ImageAdapter(AddImageActivity.this,bitmapArray));
//            }
//        });

        addImageBtn = findViewById(R.id.addImageBtn);
        addImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        });
    }

    public static void mark(Bitmap src, String watermark, int size, boolean underline, Context context, ArrayList<Bitmap> bitmapArray) {
        int w = src.getWidth();
        int h = src.getHeight();
        Bitmap result = Bitmap.createBitmap(w, h, src.getConfig());

        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(src, 0, 0, null);

        Paint paint = new Paint();
        paint.setColor(ContextCompat.getColor(context.getApplicationContext(),R.color.appBlue));
        paint.setAlpha(255);
        paint.setTextSize(5);
        paint.setAntiAlias(true);
        paint.setFakeBoldText(true);
        paint.setUnderlineText(underline);
        canvas.drawText(watermark, 3,src.getHeight()-30, paint);

        bitmapArray.add(result);
//        return result;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");

            Date currentTime = Calendar.getInstance().getTime();

            mark(photo,currentTime.toString(),20,false,AddImageActivity.this, bitmapArray);


//            bitmapArray.add();
//            final CustomView customView1 = new CustomView(getBaseContext());
//            final CustomView customView2 = new CustomView(getBaseContext());
//            final LinearLayout container = (LinearLayout) findViewById(R.id.im);
//            container.addView(customView1);
//            container.addView(customView2);
            imagesGrid.setAdapter(new ImageAdapter(this,bitmapArray));
        }
    }

    public static class CustomView extends FrameLayout {

        private View mRoot;
        private ImageView mImgPhoto;
        private ImageView mBtnClose;

        private Context mContext;

        public CustomView(final Context context) {
            this(context, null);
        }

        public CustomView(final Context context, final AttributeSet attrs) {
            this(context, attrs, 0);
        }

        public CustomView(final Context context, final AttributeSet attrs, final int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            init(context);
        }

        private void init(final Context context) {
            if (isInEditMode())
                return;

            final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View customView = null;

            if (inflater != null)
                customView = inflater.inflate(R.layout.added_image_layout, this);

            if (customView == null)
                return;

            mRoot = customView.findViewById(R.id.root);
            mImgPhoto = (ImageView) customView.findViewById(R.id.img_photo);
            mBtnClose = customView.findViewById(R.id.btn_close);
        }

        public View getRoot() {
            return mRoot;
        }

        public ImageView getImgPhoto() {
            return mImgPhoto;
        }

        public View getBtnClose() {
            return mBtnClose;
        }
    }

    public class ImageAdapter extends BaseAdapter {

        private Context context;

        Drawable drawable;
        private ArrayList<Bitmap> bitmapList;

        public ImageAdapter(Context context, ArrayList<Bitmap> bitmapList) {
            this.context = context;
            this.bitmapList = bitmapList;
            this.drawable =  ContextCompat.getDrawable(context,R.drawable.addimageicon);
        }

        public int getCount() {
            return this.bitmapList.size();
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
//            ViewGroup newjj = new ViewGroup() {
//                @Override
//                protected void onLayout(boolean b, int i, int i1, int i2, int i3) {
//
//                }
//            };

            CustomView customView = new CustomView(context);;

//            ImageView imageView;
//            ImageView draw = new ImageView(context);
//            draw.setImageDrawable(drawable);

            if (convertView == null) {
//                imageView = new ImageView(this.context);
////                imageView.setImageDrawable(drawable);
//                imageView.setLayoutParams(new GridView.LayoutParams(400, 400));
//                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);


//                customView = new CustomView(context);
                customView.setLayoutParams(new GridView.LayoutParams(500, 500));
//
                customView.mImgPhoto.setScaleType(ImageView.ScaleType.CENTER_CROP);
//                customView.mBtnClose.setScaleType(ImageView.ScaleType.FIT_XY);
            } else {
//                imageView = (ImageView) convertView;
                customView = (CustomView) convertView;
            }


//            imageView.setImageBitmap(this.bitmapList.get(position));

            customView.mImgPhoto.setImageBitmap(this.bitmapList.get(position));
            customView.mBtnClose.setTag(position);

            customView.mBtnClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    bitmapArray.remove(position);
                    imagesGrid.setAdapter(null);
                    imagesGrid.setAdapter(new ImageAdapter(AddImageActivity.this,bitmapArray));

                }
            });

//            customView.mBtnClose.setImageResource(R.drawable.closecircle);


//            newjj.addView(imageView);
//            newjj.addView(draw);

            return customView;
//            return newjj;
        }
    }
}