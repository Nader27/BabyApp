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
                    if(dataSnapshot.getChildrenCount() == 0){
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
                    if(dataSnapshot.getChildrenCount() == 0){
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
        public String image_uri;
        public String name;
        public String username;
        public String type_id;
        //ex:public ForeignClass ForeignClass
        public User_Types user_types;

        public Users() {
            user_types = new User_Types();
            //ex ForeignClass = new ForeignClass();
        }

        //region Getter & Setter

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getImage_uri() {
            return image_uri;
        }

        public void setImage_uri(String image_uri) {
            this.image_uri = image_uri;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getType_id() {
            return type_id;
        }

        public void setType_id(String type_id) {
            this.type_id = type_id;
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
                    user_types.Findbykey(obj.type_id, Data -> {
                        obj.user_types = Data;
                        listener.onSuccess(obj);
                    });
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

                        user_types.Findbykey(obj.type_id, Data -> {
                            obj.user_types = Data;
                            Items.add(obj);
                            if (!iterator.hasNext()) {
                                listener.onSuccess(Items);
                            }
                        });
                    }
                    if(dataSnapshot.getChildrenCount() == 0){
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
                        user_types.Findbykey(obj.type_id, Data -> {
                            obj.user_types = Data;
                            Items.add(obj);
                            if (!iterator.hasNext()) {
                                listener.onSuccess(Items);
                            }
                        });
                    }
                    if(dataSnapshot.getChildrenCount() == 0){
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
            Image_uri("image_uri"),
            Name("name"),
            Username("username"),
            Type_id("type_id");

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

    //region User_Types
    public static class User_Types {
        //ex: private static final DatabaseReference Ref = myRootRef.child("TableName");
        public static final DatabaseReference Ref = myRootRef.child("User_Types");

        public String Key;
        //ex: public String Column;
        public String name;
        //ex:public ForeignClass ForeignClass

        public User_Types() {
            //ex ForeignClass = new ForeignClass();
        }

        //region Getter & Setter

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
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

        public void Findbykey(String key, final OnGetDataListener<User_Types> listener) {
            Ref.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    //ex: final ClassName obj = new Teams();
                    final User_Types obj = new User_Types();
                    obj.Key = dataSnapshot.getKey();
                    for (Table T : Table.values()) {
                        setbyName(obj, T.name(), dataSnapshot.child(T.text).getValue().toString());
                    }
                    //if no foreign key
                    listener.onSuccess(obj);
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

        public void Where(Table table, String Value, final OnGetDataListListener<User_Types> listener) {
            //ex: final List<ClassName> Items = new ArrayList<>();
            final List<User_Types> Items = new ArrayList<>();
            Query query = Ref.orderByChild(table.text).equalTo(Value);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    final Iterator iterator = dataSnapshot.getChildren().iterator();
                    while (iterator.hasNext()) {
                        DataSnapshot postSnapshot = (DataSnapshot) iterator.next();
                        //ex: final ClassName obj = new Teams();
                        final User_Types obj = new User_Types();
                        obj.Key = postSnapshot.getKey();
                        for (Table T : Table.values()) {
                            setbyName(obj, T.name(), postSnapshot.child(T.text).getValue().toString());
                        }
                        //if no foreign key
                        Items.add(obj);
                        if (!iterator.hasNext()) {
                            listener.onSuccess(Items);
                        }
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
                    if(dataSnapshot.getChildrenCount() == 0){
                        listener.onSuccess(Items);
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        public void Tolist(final OnGetDataListListener<User_Types> listener) {
            //ex: final List<ClassName> Items = new ArrayList<>();
            final List<User_Types> Items = new ArrayList<>();
            Ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(final DataSnapshot dataSnapshot) {
                    final Iterator iterator = dataSnapshot.getChildren().iterator();
                    while (iterator.hasNext()) {
                        DataSnapshot postSnapshot = (DataSnapshot) iterator.next();
                        //ex: final ClassName obj = new Teams();
                        final User_Types obj = new User_Types();
                        obj.Key = postSnapshot.getKey();
                        for (Table T : Table.values()) {
                            setbyName(obj, T.name(), postSnapshot.child(T.text).getValue().toString());
                        }
                        //if no foreign key
                        Items.add(obj);
                        if (!iterator.hasNext()) {
                            listener.onSuccess(Items);
                        }
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
                    if(dataSnapshot.getChildrenCount() == 0){
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

        private String getbyName(User_Types obj, String Name) {
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

        private void setbyName(User_Types obj, String Name, String Value) {
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
            Name("name");

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
