package com.example.gestordegastos;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import android.net.Uri;
import android.widget.ImageView;
import android.widget.Toast;

public class InterfazGastos extends AppCompatActivity {

    private SharedPreferences preferences;
    private static final String PREFS_NAME = "ExpensesPrefs";
    private static final String EXPENSES_KEY = "expenses";

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private NavigationView navigationView;

    private TextView expensesTextView;
    private ImageView imageViewProfile;
    private Button CerrarSesion;

    private FirebaseAuth rAuth;
    private DatabaseReference mDatabase;
    private StorageReference storageReference;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interfaz_gastos);

        drawerLayout = findViewById(R.id.drawerlayout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(
                this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        navigationView = findViewById(R.id.navigationView);
        actionBarDrawerToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        navigationView.setNavigationItemSelectedListener(item -> {
            handleMenuItemClick(item.getItemId());
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        expensesTextView = findViewById(R.id.CantidadInicial2);
        TextView historialTextView = findViewById(R.id.Historial);
        preferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        imageViewProfile = navigationView.getHeaderView(0).findViewById(R.id.perfil);
        TextView textViewNombre = navigationView.getHeaderView(0).findViewById(R.id.textViewNombre);

        rAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        FirebaseUser user = rAuth.getCurrentUser();

        if (user != null) {
            String userID = user.getUid();
            mostrarCantidadInicial(userID);
            mostrarNombreDeUsuario(userID, textViewNombre);
            mostrarImagenDePerfil(userID);

            // Obtener la cantidad inicial desde Realtime Database y mostrarla en el TextView
            mostrarCantidadInicial(userID);

            // Obtener y mostrar los gastos desde Realtime Database
            mostrarHistorialGastos(userID, historialTextView);
        }
    }

    private void mostrarNombreDeUsuario(String userID, TextView textViewNombre) {
        DatabaseReference userRef = mDatabase.child(userID);

        userRef.child("Nombre").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String nombre = dataSnapshot.getValue(String.class);

                    // Mostrar el nombre en el TextView
                    textViewNombre.setText(nombre);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void handleMenuItemClick(int itemId) {
        if (itemId == R.id.calendar2) {
            Intent intent = new Intent(InterfazGastos.this, Calendario.class);
            startActivity(intent);
        } else if (itemId == R.id.CerrarSesion2) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(InterfazGastos.this, InicioSesion.class));
            finish();
        } else if (itemId == R.id.Dash){
            Intent intent = new Intent(InterfazGastos.this, DashboardActivity.class);
            startActivity(intent);
        } else if (itemId == R.id.CambiarImagenPerfil) {
            openGallery();
        }
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST);
    }

    private void uploadProfileImage(String userID) {
        if (imageUri != null) {
            StorageReference imageRef = storageReference.child("profile_images").child(userID);

            imageRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        Toast.makeText(this, "La imagen se subio con exito", Toast.LENGTH_SHORT).show();
                        mostrarImagenDePerfil(userID);
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Ocurrio un error, vuelve a intentarlo", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void mostrarImagenDePerfil(String userID) {
        StorageReference profileImageRef = storageReference.child("profile_images").child(userID);

        // Obtener la URL de la imagen
        profileImageRef.getDownloadUrl()
                .addOnSuccessListener(uri -> {
                    String imageUrl = uri.toString();
                    Log.d("ImageURL", imageUrl); // Imprime la URL de la imagen en el Log

                    // Cargar la imagen en el ImageView con Glide
                    Glide.with(this)
                            .load(imageUrl)
                            .placeholder(R.drawable.gastos2)
                            .error(R.drawable.gastos2)
                            .into(imageViewProfile);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "No tiene foto de perfil", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();

            // Subir la imagen de perfil a Firebase Storage
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                uploadProfileImage(user.getUid());
            }
        }
    }

    private void mostrarCantidadInicial(String userID) {
        DatabaseReference userRef = mDatabase.child(userID);

        userRef.child("CantidadInicial1").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    double cantidadInicial = dataSnapshot.getValue(Double.class);
                    expensesTextView.setText("$ " + cantidadInicial);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Manejar errores de lectura desde la base de datos
                // Puedes agregar un Toast o log para indicar el error si es necesario
            }
        });
    }

    private void mostrarHistorialGastos(String userID, TextView historialTextView) {
        DatabaseReference userRef = mDatabase.child(userID).child("Gastos");

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                StringBuilder historialBuilder = new StringBuilder();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Gasto gasto = snapshot.getValue(Gasto.class);

                    // Agregar la información del gasto al StringBuilder
                    historialBuilder.append(gasto.getFecha())
                            .append(": -$")
                            .append(gasto.getCantidad())
                            .append("  :  ")
                            .append(gasto.getDescripcion())
                            .append("\n");
                }

                // Establecer el texto en el TextView
                historialTextView.setText(historialBuilder.toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Manejar errores de lectura desde la base de datos
                // Puedes agregar un Toast o log para indicar el error si es necesario
            }
        });
    }

    public void AgregarMovimiento(View view) {
        Intent intent = new Intent(view.getContext(), AgregarGastos.class);
        view.getContext().startActivity(intent);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }

    public void Ingresos(View view) {
        Intent intent = new Intent(view.getContext(), InterfazIngresos.class);
        view.getContext().startActivity(intent);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }

    public void VerCalendario(View view) {
        Intent intent = new Intent(view.getContext(), Calendario.class);
        view.getContext().startActivity(intent);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }

    private void displayExpenses() {
        String expenseList = preferences.getString(EXPENSES_KEY, "");
        expensesTextView.setText(expenseList);
    }
}
