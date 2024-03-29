package com.example.canteenapp.OrderList;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.canteenapp.AdminPanel;
import com.example.canteenapp.R;
import com.example.canteenapp.Util.CanteenUtil;
import com.example.canteenapp.constant.FireBaseConstant;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class OrderIteam extends AppCompatActivity {
  private FirebaseDatabase firebaseDatabase;
  private DatabaseReference databaseReference,databaseReferenceMasterRecord, databaseReference2, databaseReference4, databaseReference11, databaseReferenceCancleConfromOrder, databaseReferencesuser;
  RecyclerView recyclerView;
  FirebaseRecyclerAdapter<ModelOrder, OrderViewHolder> firebaseRecyclerAdapter;
  long maxId = 0;
  TextView  title;
  ShimmerFrameLayout shimmerFrameLayout;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_order_iteam);
    firebaseDatabase = FirebaseDatabase.getInstance();
    databaseReference = firebaseDatabase.getReference(FireBaseConstant.CONFIRMED);
    databaseReferenceCancleConfromOrder = firebaseDatabase.getReference(FireBaseConstant.CONFIRMED);
    databaseReference11 = firebaseDatabase.getReference(FireBaseConstant.TODAYS_HITS);
    databaseReference4 = firebaseDatabase.getReference().child(FireBaseConstant.TOTAL_MONEY_OF_USER);
    databaseReference2 = firebaseDatabase.getReference(FireBaseConstant.HISTORY);
    databaseReferencesuser = firebaseDatabase.getReference().child(FireBaseConstant.USERS);
    databaseReferenceMasterRecord = firebaseDatabase.getReference().child(FireBaseConstant.MASTER_RECORD);
    title=findViewById(R.id.orderItemTitle);
    shimmerFrameLayout=findViewById(R.id.orderIteamShimmerLayout);

    databaseReference2.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot snapshot) {
        title.setVisibility(View.VISIBLE);
        shimmerFrameLayout.stopShimmerAnimation();
        shimmerFrameLayout.setVisibility(View.GONE);
        if (snapshot.exists()) {
          maxId = snapshot.getChildrenCount();
        }
      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {

      }
    });
    recyclerView = (RecyclerView) findViewById(R.id.recyclervireforiteam);
    recyclerView.setHasFixedSize(true);
    recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

  }

  @Override
  public void onStart() {
    super.onStart();
    shimmerFrameLayout.startShimmerAnimation();
    title.setVisibility(View.GONE);
    final FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<ModelOrder>().setQuery(databaseReference, ModelOrder.class).build();
    firebaseRecyclerAdapter = new
            FirebaseRecyclerAdapter<ModelOrder, OrderViewHolder>(options) {
              @Override
              protected void onBindViewHolder(@NonNull final OrderViewHolder orderViewHolder, final int i, @NonNull final ModelOrder modelorder) {
                orderViewHolder.username.setText(modelorder.getName());
                orderViewHolder.topfoodname.setText(modelorder.getFoodName());
                orderViewHolder.topfoodcount.setText(modelorder.getFoodCount());
                orderViewHolder.topfoodprice.setText(modelorder.getFoodPrize());
                orderViewHolder.total.setText(modelorder.getTotal());
                orderViewHolder.tokenadmin.setText(modelorder.getUserId());
                orderViewHolder.time.setText(CanteenUtil.ConvertMilliSecondsToPrettyTime(modelorder.getTime()));
                orderViewHolder.cancel.setOnClickListener(v -> {
                  databaseReferenceCancleConfromOrder.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                      if (snapshot != null) {
                        for (DataSnapshot children : snapshot.getChildren()) {
                          if (children.child("uniqueId").getValue().toString() == modelorder.getUniqueId()) {
                            databaseReferenceCancleConfromOrder.child(children.getKey()).removeValue();
                          }
                        }
                      }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                  });
                  databaseReferencesuser.child(modelorder.getUniqueId()).child("id").removeValue();
                });
                orderViewHolder.orderredy.setOnClickListener(v -> {
                  databaseReference.child(modelorder.getUserId()).child(FireBaseConstant.NOTIFICATION_ID).setValue(1);
                  Toast.makeText(getApplicationContext(), "DONE", Toast.LENGTH_SHORT).show();
                });
                orderViewHolder.done.setOnClickListener(view -> {
                  databaseReferencesuser.child(modelorder.getUniqueId()).child("id").removeValue();
                  databaseReferenceMasterRecord.child(String.valueOf(maxId + 1)).child("userName").setValue(modelorder.getName());
                  databaseReferenceMasterRecord.child(String.valueOf(maxId + 1)).child("foodName").setValue(modelorder.getFoodName());
                  databaseReferenceMasterRecord.child(String.valueOf(maxId + 1)).child("foodCount").setValue(modelorder.getFoodCount());
                  databaseReferenceMasterRecord.child(String.valueOf(maxId + 1)).child("foodPrize").setValue(modelorder.getFoodPrize());
                  databaseReferenceMasterRecord.child(String.valueOf(maxId + 1)).child("total").setValue(modelorder.getTotal());
                  databaseReferenceMasterRecord.child(String.valueOf(maxId + 1)).child("time").setValue(modelorder.getTime());

                  databaseReference2.child(String.valueOf(maxId + 1)).child("userName").setValue(modelorder.getName());
                  databaseReference2.child(String.valueOf(maxId + 1)).child("foodName").setValue(modelorder.getFoodName());
                  databaseReference2.child(String.valueOf(maxId + 1)).child("foodCount").setValue(modelorder.getFoodCount());
                  databaseReference2.child(String.valueOf(maxId + 1)).child("foodPrize").setValue(modelorder.getFoodPrize());
                  databaseReference2.child(String.valueOf(maxId + 1)).child("total").setValue(modelorder.getTotal());
                  databaseReference2.child(String.valueOf(maxId + 1)).child("time").setValue(modelorder.getTime());
                  databaseReference2.child(String.valueOf(maxId + 1)).child("comment").setValue("REMOVED BY ADMIN");
                  String a = modelorder.getFoodName();
                  String b = modelorder.getFoodCount();
                  a = a.replaceAll("\n", ",");
                  b = b.replaceAll("\n", ",");
                  a = a.replaceFirst(",", "");
                  b = b.replaceFirst(",", "");
                  final String[] aa = a.split(",");
                  final String[] bb = b.split(",");
                  databaseReference11.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                      if (snapshot.getValue() != null) {
                        int count = 0;
                        for (String i1 : aa) {
                          if (snapshot.child(aa[count]).exists()) {
                            int value = Integer.parseInt(snapshot.child(aa[count]).getValue().toString());
                            databaseReference11.child(aa[count]).setValue(Integer.parseInt(bb[count]) + value);
                          } else {
                            databaseReference11.child(aa[count]).setValue(bb[count]);
                          }
                          count++;
                        }
                      } else {
                        int count = 0;
                        for (String i1 : aa) {
                          databaseReference11.child(aa[count]).setValue(bb[count]);
                          count++;
                        }
                      }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                  });

                  databaseReference4.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                      if (snapshot.hasChild(modelorder.getUniqueId())) {
                        int total2 = Integer.parseInt(snapshot.child(modelorder.getUniqueId()).getValue().toString());
                        databaseReference4.child(modelorder.getUniqueId()).setValue(String.valueOf(Integer.parseInt(modelorder.getTotal()) + total2));//not made for same username
                      } else {
                        databaseReference4.child(modelorder.getUniqueId()).setValue(modelorder.getTotal());//not made for same username
                      }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                  });
                  databaseReference.child(modelorder.getUserId()).removeValue();

                });
              }

              @NonNull
              @Override
              public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.orderiteamtemplate, parent, false);
                return new OrderViewHolder(view);

              }
            };

    firebaseRecyclerAdapter.startListening();
    recyclerView.setItemViewCacheSize(900);
    firebaseRecyclerAdapter.notifyDataSetChanged();
    recyclerView.setAdapter(firebaseRecyclerAdapter);
  }

  @Override
  public void onBackPressed() {
    super.onBackPressed();
    finishAffinity();
    Intent intent = new Intent(getApplicationContext(), AdminPanel.class);
    startActivity(intent);
  }
}