package fcih.babyapp;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class FireBaseHelper {
    private static final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static final DatabaseReference myRootRef = database.getReference();


    public interface OnGetDataListener<T> {
        void onSuccess(T Data);
    }

    public interface OnGetDataListListener<T> {
        void onSuccess(List<T> Data);
    }

    //region Temp
    private static class ClassName {
        //TODO:-----------------------------------------------------
        //TODO:Add Ref To Tables and make public class & Refactor ClassName
        //ex: private static final DatabaseReference Ref = myRootRef.child("TableName");
        public static final DatabaseReference Ref = myRootRef.child("TableName");

        public String Key;
        //TODO:Add Columns
        //ex: public String Column;
        public String Column;
        //TODO:Add Foreign
        //ex:public ForeignClass ForeignClass

        public ClassName() {
            //TODO:ForeignClasses
            //ex ForeignClass = new ForeignClass();
        }

        //region Getter & Setter
        //TODO:Create Getter & Setter

        //endregion

        public String Add() {
            Map<String, String> Values = new HashMap<>();
            for (Table T : Table.values()) {
                Values.put(T.text, getbyName(this, T.name()));
            }
            DatabaseReference myref = Ref.push();
            myref.setValue(Values);
            return Key = myref.getKey();

        }

        public void Add(String Key) {
            Map<String, String> Values = new HashMap<>();
            for (Table T : Table.values()) {
                Values.put(T.text, getbyName(this, T.name()));
            }
            Ref.child(Key).setValue(Values);
        }

        public void Update() {
            Map<String, String> Values = new HashMap<>();
            for (Table T : Table.values()) {
                Values.put(T.text, getbyName(this, T.name()));
            }
            Ref.child(Key).setValue(Values);
        }

        public void Update(String key) {
            Map<String, String> Values = new HashMap<>();
            for (Table T : Table.values()) {
                Values.put(T.text, getbyName(this, T.name()));
            }
            Ref.child(key).setValue(Values);
        }

        public void Findbykey(String key, final OnGetDataListener<ClassName> listener) {
            Ref.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    //ex: final ClassName obj = new Teams();
                    final ClassName obj = new ClassName();
                    obj.Key = dataSnapshot.getKey();
                    for (Table T : Table.values()) {
                        setbyName(obj, T.name(), dataSnapshot.child(T.text).getValue().toString());
                    }
                    //if no foreign key
                    listener.onSuccess(obj);
                    //TODO:Foreign Keys
                    //ex:
                    //ForeignClass.findbykey(obj.ForeignKey, new OnGetDataListener() {
                    //@Override
                    //public void onSuccess(Object Data) {
                    //    obj.ForeignClass = (FireBaseHelper.ForeignClass) Data;
                    //    listener.onSuccess(obj);
                    //}
                    //});
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        public void Where(Table table, String Value, final OnGetDataListListener<ClassName> listener) {
            //ex: final List<ClassName> Items = new ArrayList<>();
            final List<ClassName> Items = new ArrayList<>();
            Query query = Ref.orderByChild(table.text).equalTo(Value);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    final Iterator iterator = dataSnapshot.getChildren().iterator();
                    while (iterator.hasNext()) {
                        DataSnapshot postSnapshot = (DataSnapshot) iterator.next();
                        //ex: final ClassName obj = new Teams();
                        final ClassName obj = new ClassName();
                        obj.Key = postSnapshot.getKey();
                        for (Table T : Table.values()) {
                            setbyName(obj, T.name(), postSnapshot.child(T.text).getValue().toString());
                        }
                        //if no foreign key
                        Items.add(obj);
                        if (!iterator.hasNext()) {
                            listener.onSuccess(Items);
                        }
                        //TODO:Foreign Keys
                        //ex:
                        //ForeignClass.findbykey(obj.ForeignKey, new OnGetDataListener() {
                        //@Override
                        //public void onSuccess(Object Data) {
                        //    obj.ForeignClass = (FireBaseHelper.ForeignClass) Data;
                        //    Items.add(obj);
                        //if (!iterator.hasNext()) {
                        //    listener.onSuccess(Items);
                        //}
                        //}
                        //});
                    }
                    if (dataSnapshot.getChildrenCount() == 0) {
                        listener.onSuccess(Items);
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        public void Tolist(final OnGetDataListListener<ClassName> listener) {
            //ex: final List<ClassName> Items = new ArrayList<>();
            final List<ClassName> Items = new ArrayList<>();
            Ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(final DataSnapshot dataSnapshot) {
                    final Iterator iterator = dataSnapshot.getChildren().iterator();
                    while (iterator.hasNext()) {
                        DataSnapshot postSnapshot = (DataSnapshot) iterator.next();
                        //ex: final ClassName obj = new Teams();
                        final ClassName obj = new ClassName();
                        obj.Key = postSnapshot.getKey();
                        for (Table T : Table.values()) {
                            setbyName(obj, T.name(), postSnapshot.child(T.text).getValue().toString());
                        }
                        //if no foreign key
                        Items.add(obj);
                        if (!iterator.hasNext()) {
                            listener.onSuccess(Items);
                        }
                        //TODO:Foreign Keys
                        //ex:
                        //ForeignClass.findbykey(obj.ForeignKey, new OnGetDataListener() {
                        //@Override
                        //public void onSuccess(Object Data) {
                        //    obj.ForeignClass = (FireBaseHelper.ForeignClass) Data;
                        //    Items.add(obj);
                        //if (!iterator.hasNext()) {
                        //    listener.onSuccess(Items);
                        //}
                        //}
                        //});
                    }
                    if (dataSnapshot.getChildrenCount() == 0) {
                        listener.onSuccess(Items);
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        public void Remove(String Key) {
            Ref.child(Key).removeValue();
        }

        private String getbyName(ClassName obj, String Name) {
            String Value = "";
            try {
                Method method = getClass().getDeclaredMethod("get" + Name);
                Object value = method.invoke(obj);
                Value = (String) value;

            } catch (SecurityException | NoSuchMethodException | IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
            return Value;
        }

        private void setbyName(ClassName obj, String Name, String Value) {
            try {
                Class[] cArg = new Class[1];
                cArg[0] = String.class;
                Method method = getClass().getDeclaredMethod("set" + Name, cArg);
                method.invoke(obj, Value);
            } catch (SecurityException | NoSuchMethodException | IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        public enum Table {
            //TODO:Add Columns
            //ex:Column("ColumnName"),
            Column("ColumnName");

            public final String text;

            Table(final String text) {
                this.text = text;
            }

            @Override
            public String toString() {
                return text;
            }
        }
    }
    //endregion

    //region Users
    public static class Users {
        //ex: private static final DatabaseReference Ref = myRootRef.child("TableName");
        public static final DatabaseReference Ref = myRootRef.child("Users");

        public String Key;
        //ex: public String Column;
        public String email;
        public String image;
        public String name;
        public String username;
        public String country;
        public String birth;
        public String city;
        public String gender;
        //ex:public ForeignClass ForeignClass

        public Users() {
            //ex ForeignClass = new ForeignClass();
        }

        //region Getter & Setter

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public String getBirth() {
            return birth;
        }

        public void setBirth(String birth) {
            this.birth = birth;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }


        //endregion

        public String Add() {
            Map<String, String> Values = new HashMap<>();
            for (Table T : Table.values()) {
                Values.put(T.text, getbyName(this, T.name()));
            }
            DatabaseReference myref = Ref.push();
            myref.setValue(Values);
            return Key = myref.getKey();

        }

        public void Add(String Key) {
            Map<String, String> Values = new HashMap<>();
            for (Table T : Table.values()) {
                Values.put(T.text, getbyName(this, T.name()));
            }
            Ref.child(Key).setValue(Values);
        }

        public void Update() {
            Map<String, String> Values = new HashMap<>();
            for (Table T : Table.values()) {
                Values.put(T.text, getbyName(this, T.name()));
            }
            Ref.child(Key).setValue(Values);
        }

        public void Update(String key) {
            Map<String, String> Values = new HashMap<>();
            for (Table T : Table.values()) {
                Values.put(T.text, getbyName(this, T.name()));
            }
            Ref.child(key).setValue(Values);
        }

        public void Findbykey(String key, final OnGetDataListener<Users> listener) {
            Ref.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    //ex: final ClassName obj = new Teams();
                    final Users obj = new Users();
                    obj.Key = dataSnapshot.getKey();
                    for (Table T : Table.values()) {
                        setbyName(obj, T.name(), dataSnapshot.child(T.text).getValue().toString());
                    }
                    //if no foreign key
                    //listener.onSuccess(obj);
                    //ex:
                    listener.onSuccess(obj);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        public void Where(Table table, String Value, final OnGetDataListListener<Users> listener) {
            //ex: final List<ClassName> Items = new ArrayList<>();
            final List<Users> Items = new ArrayList<>();
            Query query = Ref.orderByChild(table.text).equalTo(Value);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    final Iterator iterator = dataSnapshot.getChildren().iterator();
                    while (iterator.hasNext()) {
                        DataSnapshot postSnapshot = (DataSnapshot) iterator.next();
                        //ex: final ClassName obj = new Teams();
                        final Users obj = new Users();
                        obj.Key = postSnapshot.getKey();
                        for (Table T : Table.values()) {
                            setbyName(obj, T.name(), postSnapshot.child(T.text).getValue().toString());
                        }
                        //if no foreign key

                        Items.add(obj);
                        if (!iterator.hasNext()) {
                            listener.onSuccess(Items);
                        }
                    }
                    if (dataSnapshot.getChildrenCount() == 0) {
                        listener.onSuccess(Items);
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        public void Tolist(final OnGetDataListListener<Users> listener) {
            //ex: final List<ClassName> Items = new ArrayList<>();
            final List<Users> Items = new ArrayList<>();
            Ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(final DataSnapshot dataSnapshot) {
                    final Iterator iterator = dataSnapshot.getChildren().iterator();
                    while (iterator.hasNext()) {
                        DataSnapshot postSnapshot = (DataSnapshot) iterator.next();
                        //ex: final ClassName obj = new Teams();
                        final Users obj = new Users();
                        obj.Key = postSnapshot.getKey();
                        for (Table T : Table.values()) {
                            setbyName(obj, T.name(), postSnapshot.child(T.text).getValue().toString());
                        }
                        //if no foreign key
                        if (!iterator.hasNext()) {
                            listener.onSuccess(Items);
                        }
                    }
                    if (dataSnapshot.getChildrenCount() == 0) {
                        listener.onSuccess(Items);
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        public void Remove(String Key) {
            Ref.child(Key).removeValue();
        }

        private String getbyName(Users obj, String Name) {
            String Value = "";
            try {
                Method method = getClass().getDeclaredMethod("get" + Name);
                Object value = method.invoke(obj);
                Value = (String) value;

            } catch (SecurityException | NoSuchMethodException | IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
            return Value;
        }

        private void setbyName(Users obj, String Name, String Value) {
            try {
                Class[] cArg = new Class[1];
                cArg[0] = String.class;
                Method method = getClass().getDeclaredMethod("set" + Name, cArg);
                method.invoke(obj, Value);
            } catch (SecurityException | NoSuchMethodException | IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        public enum Table {
            //ex:Column("ColumnName"),
            Email("email"),
            Image("image"),
            Name("name"),
            Username("username"),
            Country("country"),
            City("city"),
            Birth("birth"),
            Gender("gender");

            public final String text;

            Table(final String text) {
                this.text = text;
            }

            @Override
            public String toString() {
                return text;
            }
        }
    }
    //endregion

    //region Children
    public static class Children {
        //ex: private static final DatabaseReference Ref = myRootRef.child("TableName");
        public static final DatabaseReference Ref = myRootRef.child("Children");

        public String Key;
        //ex: public String Column;
        public String name;
        public String image;
        public String gender;
        public String birth;
        public String parent;
        //ex:public ForeignClass ForeignClass
        public Users Parent;

        public Children() {
            //ex ForeignClass = new ForeignClass();
            Parent = new Users();
        }

        //region Getter & Setter

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        public String getBirth() {
            return birth;
        }

        public void setBirth(String birth) {
            this.birth = birth;
        }

        public String getParent() {
            return parent;
        }

        public void setParent(String parent) {
            this.parent = parent;
        }


        //endregion

        public String Add() {
            Map<String, String> Values = new HashMap<>();
            for (Table T : Table.values()) {
                Values.put(T.text, getbyName(this, T.name()));
            }
            DatabaseReference myref = Ref.push();
            myref.setValue(Values);
            return Key = myref.getKey();

        }

        public void Add(String Key) {
            Map<String, String> Values = new HashMap<>();
            for (Table T : Table.values()) {
                Values.put(T.text, getbyName(this, T.name()));
            }
            Ref.child(Key).setValue(Values);
        }

        public void Update() {
            Map<String, String> Values = new HashMap<>();
            for (Table T : Table.values()) {
                Values.put(T.text, getbyName(this, T.name()));
            }
            Ref.child(Key).setValue(Values);
        }

        public void Update(String key) {
            Map<String, String> Values = new HashMap<>();
            for (Table T : Table.values()) {
                Values.put(T.text, getbyName(this, T.name()));
            }
            Ref.child(key).setValue(Values);
        }

        public void Findbykey(String key, final OnGetDataListener<Children> listener) {
            Ref.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    //ex: final ClassName obj = new Teams();
                    final Children obj = new Children();
                    obj.Key = dataSnapshot.getKey();
                    for (Table T : Table.values()) {
                        setbyName(obj, T.name(), dataSnapshot.child(T.text).getValue().toString());
                    }
                    //if no foreign key
                    obj.Parent.Findbykey(obj.Key, Data -> {
                        obj.Parent = Data;
                        listener.onSuccess(obj);
                    });
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        public void Where(Table table, String Value, final OnGetDataListListener<Children> listener) {
            //ex: final List<ClassName> Items = new ArrayList<>();
            final List<Children> Items = new ArrayList<>();
            Query query = Ref.orderByChild(table.text).equalTo(Value);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    final Iterator iterator = dataSnapshot.getChildren().iterator();
                    while (iterator.hasNext()) {
                        DataSnapshot postSnapshot = (DataSnapshot) iterator.next();
                        //ex: final ClassName obj = new Teams();
                        final Children obj = new Children();
                        obj.Key = postSnapshot.getKey();
                        for (Table T : Table.values()) {
                            setbyName(obj, T.name(), postSnapshot.child(T.text).getValue().toString());
                        }
                        obj.Parent.Findbykey(obj.Key, Data -> {
                            obj.Parent = Data;
                            Items.add(obj);
                            if (!iterator.hasNext()) {
                                listener.onSuccess(Items);
                            }
                        });
                    }
                    if (dataSnapshot.getChildrenCount() == 0) {
                        listener.onSuccess(Items);
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        public void Tolist(final OnGetDataListListener<Children> listener) {
            //ex: final List<ClassName> Items = new ArrayList<>();
            final List<Children> Items = new ArrayList<>();
            Ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(final DataSnapshot dataSnapshot) {
                    final Iterator iterator = dataSnapshot.getChildren().iterator();
                    while (iterator.hasNext()) {
                        DataSnapshot postSnapshot = (DataSnapshot) iterator.next();
                        //ex: final ClassName obj = new Teams();
                        final Children obj = new Children();
                        obj.Key = postSnapshot.getKey();
                        for (Table T : Table.values()) {
                            setbyName(obj, T.name(), postSnapshot.child(T.text).getValue().toString());
                        }
                        //if no foreign key
                        obj.Parent.Findbykey(obj.Key, Data -> {
                            obj.Parent = Data;
                            Items.add(obj);
                            if (!iterator.hasNext()) {
                                listener.onSuccess(Items);
                            }
                        });
                    }
                    if (dataSnapshot.getChildrenCount() == 0) {
                        listener.onSuccess(Items);
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        public void Remove(String Key) {
            Ref.child(Key).removeValue();
        }

        private String getbyName(Children obj, String Name) {
            String Value = "";
            try {
                Method method = getClass().getDeclaredMethod("get" + Name);
                Object value = method.invoke(obj);
                Value = (String) value;

            } catch (SecurityException | NoSuchMethodException | IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
            return Value;
        }

        private void setbyName(Children obj, String Name, String Value) {
            try {
                Class[] cArg = new Class[1];
                cArg[0] = String.class;
                Method method = getClass().getDeclaredMethod("set" + Name, cArg);
                method.invoke(obj, Value);
            } catch (SecurityException | NoSuchMethodException | IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        public enum Table {
            //TODO:Add Columns
            //ex:Column("ColumnName"),
            Name("name"),
            Image("image"),
            Gender("gender"),
            Birth("birth"),
            Parent("parent");

            public final String text;

            Table(final String text) {
                this.text = text;
            }

            @Override
            public String toString() {
                return text;
            }
        }
    }
    //endregion

    //region Posts
    public static class Posts {
        //ex: private static final DatabaseReference Ref = myRootRef.child("TableName");
        public static final DatabaseReference Ref = myRootRef.child("Posts");

        public String Key;
        //ex: public String Column;
        public String date;
        public String description;
        public String image;
        public String uid;
        //ex:public ForeignClass ForeignClass
        public Users Users;

        public Posts() {
            //ex ForeignClass = new ForeignClass();
            Users = new Users();
        }

        //region Getter & Setter

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }


        //endregion

        public String Add() {
            Map<String, String> Values = new HashMap<>();
            for (Table T : Table.values()) {
                Values.put(T.text, getbyName(this, T.name()));
            }
            DatabaseReference myref = Ref.push();
            myref.setValue(Values);
            return Key = myref.getKey();

        }

        public void Add(String Key) {
            Map<String, String> Values = new HashMap<>();
            for (Table T : Table.values()) {
                Values.put(T.text, getbyName(this, T.name()));
            }
            Ref.child(Key).setValue(Values);
        }

        public void Update() {
            Map<String, String> Values = new HashMap<>();
            for (Table T : Table.values()) {
                Values.put(T.text, getbyName(this, T.name()));
            }
            Ref.child(Key).setValue(Values);
        }

        public void Update(String key) {
            Map<String, String> Values = new HashMap<>();
            for (Table T : Table.values()) {
                Values.put(T.text, getbyName(this, T.name()));
            }
            Ref.child(key).setValue(Values);
        }

        public void Findbykey(String key, final OnGetDataListener<Posts> listener) {
            Ref.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    //ex: final ClassName obj = new Teams();
                    final Posts obj = new Posts();
                    obj.Key = dataSnapshot.getKey();
                    for (Table T : Table.values()) {
                        setbyName(obj, T.name(), dataSnapshot.child(T.text).getValue().toString());
                    }
                    //if no foreign key
                    obj.Users.Findbykey(obj.Key, Data -> {
                        obj.Users = Data;
                        listener.onSuccess(obj);
                    });
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        public void Where(Table table, String Value, final OnGetDataListListener<Posts> listener) {
            //ex: final List<ClassName> Items = new ArrayList<>();
            final List<Posts> Items = new ArrayList<>();
            Query query = Ref.orderByChild(table.text).equalTo(Value);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    final Iterator iterator = dataSnapshot.getChildren().iterator();
                    while (iterator.hasNext()) {
                        DataSnapshot postSnapshot = (DataSnapshot) iterator.next();
                        //ex: final ClassName obj = new Teams();
                        final Posts obj = new Posts();
                        obj.Key = postSnapshot.getKey();
                        for (Table T : Table.values()) {
                            setbyName(obj, T.name(), postSnapshot.child(T.text).getValue().toString());
                        }
                        obj.Users.Findbykey(obj.Key, Data -> {
                            obj.Users = Data;
                            Items.add(obj);
                            if (!iterator.hasNext()) {
                                listener.onSuccess(Items);
                            }
                        });
                    }
                    if (dataSnapshot.getChildrenCount() == 0) {
                        listener.onSuccess(Items);
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        public void Tolist(final OnGetDataListListener<Posts> listener) {
            //ex: final List<ClassName> Items = new ArrayList<>();
            final List<Posts> Items = new ArrayList<>();
            Ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(final DataSnapshot dataSnapshot) {
                    final Iterator iterator = dataSnapshot.getChildren().iterator();
                    while (iterator.hasNext()) {
                        DataSnapshot postSnapshot = (DataSnapshot) iterator.next();
                        //ex: final ClassName obj = new Teams();
                        final Posts obj = new Posts();
                        obj.Key = postSnapshot.getKey();
                        for (Table T : Table.values()) {
                            setbyName(obj, T.name(), postSnapshot.child(T.text).getValue().toString());
                        }
                        //if no foreign key
                        obj.Users.Findbykey(obj.Key, Data -> {
                            obj.Users = Data;
                            Items.add(obj);
                            if (!iterator.hasNext()) {
                                listener.onSuccess(Items);
                            }
                        });
                    }
                    if (dataSnapshot.getChildrenCount() == 0) {
                        listener.onSuccess(Items);
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        public void Remove(String Key) {
            Ref.child(Key).removeValue();
        }

        private String getbyName(Posts obj, String Name) {
            String Value = "";
            try {
                Method method = getClass().getDeclaredMethod("get" + Name);
                Object value = method.invoke(obj);
                Value = (String) value;

            } catch (SecurityException | NoSuchMethodException | IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
            return Value;
        }

        private void setbyName(Posts obj, String Name, String Value) {
            try {
                Class[] cArg = new Class[1];
                cArg[0] = String.class;
                Method method = getClass().getDeclaredMethod("set" + Name, cArg);
                method.invoke(obj, Value);
            } catch (SecurityException | NoSuchMethodException | IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        public enum Table {
            //TODO:Add Columns
            //ex:Column("ColumnName"),
            Date("date"),
            Image("image"),
            Description("description"),
            Uid("uid");

            public final String text;

            Table(final String text) {
                this.text = text;
            }

            @Override
            public String toString() {
                return text;
            }
        }
    }
    //endregion

    //region Likes
    public static class Likes {
        //ex: private static final DatabaseReference Ref = myRootRef.child("TableName");
        public static final DatabaseReference Ref = myRootRef.child("Likes");

        public String Key;
        //ex: public String Column;
        public String uid;
        public String postid;
        public Users User;
        public Posts Post;

        public Likes() {

            User = new Users();
            Post = new Posts();
        }

        //region Getter & Setter

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public String getPostid() {
            return postid;
        }

        public void setPostid(String postid) {
            this.postid = postid;
        }


        //endregion

        public String Add() {
            Map<String, String> Values = new HashMap<>();
            for (Table T : Table.values()) {
                Values.put(T.text, getbyName(this, T.name()));
            }
            DatabaseReference myref = Ref.push();
            myref.setValue(Values);
            return Key = myref.getKey();

        }

        public void Add(String Key) {
            Map<String, String> Values = new HashMap<>();
            for (Table T : Table.values()) {
                Values.put(T.text, getbyName(this, T.name()));
            }
            Ref.child(Key).setValue(Values);
        }

        public void Update() {
            Map<String, String> Values = new HashMap<>();
            for (Table T : Table.values()) {
                Values.put(T.text, getbyName(this, T.name()));
            }
            Ref.child(Key).setValue(Values);
        }

        public void Update(String key) {
            Map<String, String> Values = new HashMap<>();
            for (Table T : Table.values()) {
                Values.put(T.text, getbyName(this, T.name()));
            }
            Ref.child(key).setValue(Values);
        }

        public void Findbykey(String key, final OnGetDataListener<Likes> listener) {
            Ref.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    //ex: final ClassName obj = new Teams();
                    final Likes obj = new Likes();
                    obj.Key = dataSnapshot.getKey();
                    for (Table T : Table.values()) {
                        setbyName(obj, T.name(), dataSnapshot.child(T.text).getValue().toString());
                    }

                    obj.Post.Findbykey(obj.postid, Data -> {
                        obj.Post = Data;
                        obj.User.Findbykey(obj.uid, Data1 -> {
                            obj.User = Data1;
                            listener.onSuccess(obj);
                        });
                    });
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        public void Where(Table table, String Value, final OnGetDataListListener<Likes> listener) {
            //ex: final List<ClassName> Items = new ArrayList<>();
            final List<Likes> Items = new ArrayList<>();
            Query query = Ref.orderByChild(table.text).equalTo(Value);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    final Iterator iterator = dataSnapshot.getChildren().iterator();
                    while (iterator.hasNext()) {
                        DataSnapshot postSnapshot = (DataSnapshot) iterator.next();
                        //ex: final ClassName obj = new Teams();
                        final Likes obj = new Likes();
                        obj.Key = postSnapshot.getKey();
                        for (Table T : Table.values()) {
                            setbyName(obj, T.name(), postSnapshot.child(T.text).getValue().toString());
                        }
                        obj.Post.Findbykey(obj.postid, Data -> {
                            obj.Post = Data;
                            obj.User.Findbykey(obj.uid, Data1 -> {
                                obj.User = Data1;
                                Items.add(obj);
                                if (!iterator.hasNext()) {
                                    listener.onSuccess(Items);
                                }
                            });
                        });
                    }
                    if (dataSnapshot.getChildrenCount() == 0) {
                        listener.onSuccess(Items);
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        public void Tolist(final OnGetDataListListener<Likes> listener) {
            //ex: final List<ClassName> Items = new ArrayList<>();
            final List<Likes> Items = new ArrayList<>();
            Ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(final DataSnapshot dataSnapshot) {
                    final Iterator iterator = dataSnapshot.getChildren().iterator();
                    while (iterator.hasNext()) {
                        DataSnapshot postSnapshot = (DataSnapshot) iterator.next();
                        //ex: final ClassName obj = new Teams();
                        final Likes obj = new Likes();
                        obj.Key = postSnapshot.getKey();
                        for (Table T : Table.values()) {
                            setbyName(obj, T.name(), postSnapshot.child(T.text).getValue().toString());
                        }
                        //if no foreign key
                        obj.Post.Findbykey(obj.postid, Data -> {
                            obj.Post = Data;
                            obj.User.Findbykey(obj.uid, Data1 -> {
                                obj.User = Data1;
                                Items.add(obj);
                                if (!iterator.hasNext()) {
                                    listener.onSuccess(Items);
                                }
                            });
                        });
                    }
                    if (dataSnapshot.getChildrenCount() == 0) {
                        listener.onSuccess(Items);
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        public void Remove(String Key) {
            Ref.child(Key).removeValue();
        }

        private String getbyName(Likes obj, String Name) {
            String Value = "";
            try {
                Method method = getClass().getDeclaredMethod("get" + Name);
                Object value = method.invoke(obj);
                Value = (String) value;

            } catch (SecurityException | NoSuchMethodException | IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
            return Value;
        }

        private void setbyName(Likes obj, String Name, String Value) {
            try {
                Class[] cArg = new Class[1];
                cArg[0] = String.class;
                Method method = getClass().getDeclaredMethod("set" + Name, cArg);
                method.invoke(obj, Value);
            } catch (SecurityException | NoSuchMethodException | IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        public enum Table {
            //TODO:Add Columns
            //ex:Column("ColumnName"),
            Uid("uid"),
            Postid("postid");

            public final String text;

            Table(final String text) {
                this.text = text;
            }

            @Override
            public String toString() {
                return text;
            }
        }
    }
    //endregion

    //region Needs
    public static class Needs {
        //ex: private static final DatabaseReference Ref = myRootRef.child("TableName");
        public static final DatabaseReference Ref = myRootRef.child("Needs");

        public String Key;
        //ex: public String Column;
        public String uid;
        public String description;
        public String image;
        public String status;
        public String childid;

        public Children Child;
        public Users User;

        public Needs() {

            User = new Users();
            Child = new Children();
        }

        //region Getter & Setter

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getChildid() {
            return childid;
        }

        public void setChildid(String childid) {
            this.childid = childid;
        }


        //endregion

        public String Add() {
            Map<String, String> Values = new HashMap<>();
            for (Table T : Table.values()) {
                Values.put(T.text, getbyName(this, T.name()));
            }
            DatabaseReference myref = Ref.push();
            myref.setValue(Values);
            return Key = myref.getKey();

        }

        public void Add(String Key) {
            Map<String, String> Values = new HashMap<>();
            for (Table T : Table.values()) {
                Values.put(T.text, getbyName(this, T.name()));
            }
            Ref.child(Key).setValue(Values);
        }

        public void Update() {
            Map<String, String> Values = new HashMap<>();
            for (Table T : Table.values()) {
                Values.put(T.text, getbyName(this, T.name()));
            }
            Ref.child(Key).setValue(Values);
        }

        public void Update(String key) {
            Map<String, String> Values = new HashMap<>();
            for (Table T : Table.values()) {
                Values.put(T.text, getbyName(this, T.name()));
            }
            Ref.child(key).setValue(Values);
        }

        public void Findbykey(String key, final OnGetDataListener<Needs> listener) {
            Ref.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    //ex: final ClassName obj = new Teams();
                    final Needs obj = new Needs();
                    obj.Key = dataSnapshot.getKey();
                    for (Table T : Table.values()) {
                        setbyName(obj, T.name(), dataSnapshot.child(T.text).getValue().toString());
                    }

                    obj.Child.Findbykey(obj.childid, Data -> {
                        obj.Child = Data;
                        obj.User.Findbykey(obj.uid, Data1 -> {
                            obj.User = Data1;
                            listener.onSuccess(obj);
                        });
                    });
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        public void Where(Table table, String Value, final OnGetDataListListener<Needs> listener) {
            //ex: final List<ClassName> Items = new ArrayList<>();
            final List<Needs> Items = new ArrayList<>();
            Query query = Ref.orderByChild(table.text).equalTo(Value);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    final Iterator iterator = dataSnapshot.getChildren().iterator();
                    while (iterator.hasNext()) {
                        DataSnapshot postSnapshot = (DataSnapshot) iterator.next();
                        //ex: final ClassName obj = new Teams();
                        final Needs obj = new Needs();
                        obj.Key = postSnapshot.getKey();
                        for (Table T : Table.values()) {
                            setbyName(obj, T.name(), postSnapshot.child(T.text).getValue().toString());
                        }
                        obj.Child.Findbykey(obj.childid, Data -> {
                            obj.Child = Data;
                            obj.User.Findbykey(obj.uid, Data1 -> {
                                obj.User = Data1;
                                Items.add(obj);
                                if (!iterator.hasNext()) {
                                    listener.onSuccess(Items);
                                }
                            });
                        });
                    }
                    if (dataSnapshot.getChildrenCount() == 0) {
                        listener.onSuccess(Items);
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        public void Tolist(final OnGetDataListListener<Needs> listener) {
            //ex: final List<ClassName> Items = new ArrayList<>();
            final List<Needs> Items = new ArrayList<>();
            Ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(final DataSnapshot dataSnapshot) {
                    final Iterator iterator = dataSnapshot.getChildren().iterator();
                    while (iterator.hasNext()) {
                        DataSnapshot postSnapshot = (DataSnapshot) iterator.next();
                        //ex: final ClassName obj = new Teams();
                        final Needs obj = new Needs();
                        obj.Key = postSnapshot.getKey();
                        for (Table T : Table.values()) {
                            setbyName(obj, T.name(), postSnapshot.child(T.text).getValue().toString());
                        }
                        //if no foreign key
                        obj.Child.Findbykey(obj.childid, Data -> {
                            obj.Child = Data;
                            obj.User.Findbykey(obj.uid, Data1 -> {
                                obj.User = Data1;
                                Items.add(obj);
                                if (!iterator.hasNext()) {
                                    listener.onSuccess(Items);
                                }
                            });
                        });
                    }
                    if (dataSnapshot.getChildrenCount() == 0) {
                        listener.onSuccess(Items);
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        public void Remove(String Key) {
            Ref.child(Key).removeValue();
        }

        private String getbyName(Needs obj, String Name) {
            String Value = "";
            try {
                Method method = getClass().getDeclaredMethod("get" + Name);
                Object value = method.invoke(obj);
                Value = (String) value;

            } catch (SecurityException | NoSuchMethodException | IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
            return Value;
        }

        private void setbyName(Needs obj, String Name, String Value) {
            try {
                Class[] cArg = new Class[1];
                cArg[0] = String.class;
                Method method = getClass().getDeclaredMethod("set" + Name, cArg);
                method.invoke(obj, Value);
            } catch (SecurityException | NoSuchMethodException | IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        public enum Table {
            //TODO:Add Columns
            //ex:Column("ColumnName"),
            Uid("uid"),
            Description("description"),
            Image("image"),
            Status("status"),
            Childid("childid");

            public final String text;

            Table(final String text) {
                this.text = text;
            }

            @Override
            public String toString() {
                return text;
            }
        }
    }
    //endregion

}
