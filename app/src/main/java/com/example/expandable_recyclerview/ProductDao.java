package com.example.expandable_recyclerview;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface ProductDao {

    @Insert
    void insertProduct(Product product);

    @Query("SELECT * FROM LastProducts order by id desc limit 10")
    List<Product> getLastProducts();
}
