package com.rgc;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseHelper extends SQLiteOpenHelper{
	 
    private static String DB_PATH = "/data/data/com.rgc/databases/";
	
	
 
    private static String DB_NAME = "baza_danych";
 
    private SQLiteDatabase myDataBase; 

    private final Context myContext;

	private final static int DATABASE_VERSION = 6;

    public DataBaseHelper(Context context) {
 
    	super(context, DB_NAME, null, DATABASE_VERSION);
    	if(android.os.Build.VERSION.SDK_INT >= 4.2){
            DB_PATH = context.getApplicationInfo().dataDir + "/databases/";         
        } else {
           DB_PATH = "/data/data/" + context.getPackageName() + "/databases/";
        }
        this.myContext = context;
    }	
 
  /**
     * Creates a empty database on the system and rewrites it with your own database.
     * */
    public void createDataBase() throws IOException{
 
    	boolean dbExist = checkDataBase();
 
    	if(dbExist){
    		
    	}else{
 
    		
        	this.getReadableDatabase();
 
        	try {
 
    			copyDataBase();
 
    		} catch (IOException e) {
 
        		throw new Error("Error copying database");
 
        	}
    	}
 
    }
 

    private boolean checkDataBase(){
 
    	SQLiteDatabase checkDB = null;
 
    	try{
    		String myPath = DB_PATH + DB_NAME;
    		checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
 
    	}catch(SQLiteException e){
 
    		
 
    	}
 
    	if(checkDB != null){
 
    		checkDB.close();
 
    	}
 
    	return checkDB != null ? true : false;
    }
 

    private void copyDataBase() throws IOException{
 
    	InputStream myInput = myContext.getAssets().open(DB_NAME);
 
    	String outFileName = DB_PATH + DB_NAME;

    	OutputStream myOutput = new FileOutputStream(outFileName);
 
    	byte[] buffer = new byte[1024];
    	int length;
    	while ((length = myInput.read(buffer))>0){
    		myOutput.write(buffer, 0, length);
    	}
 
    	myOutput.flush();
    	myOutput.close();
    	myInput.close();
 
    }
 
    public void openDataBase() throws SQLException{
 
        String myPath = DB_PATH + DB_NAME;
    	myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
 
    }
 
    @Override
	public synchronized void close() {
 
    	    if(myDataBase != null)
    		    myDataBase.close();
 
    	    super.close();
 
	}
 
	@Override
	public void onCreate(SQLiteDatabase db) {
 
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (oldVersion<2) {
			db.execSQL("ALTER TABLE urzadzenia ADD COLUMN Tab_GPIO_Output INT DEFAULT 1 NOT NULL");
			db.execSQL("ALTER TABLE urzadzenia ADD COLUMN Tab_GPIO_Input INT DEFAULT 1 NOT NULL");
			db.execSQL("ALTER TABLE urzadzenia ADD COLUMN Tab_GPIO_Pwm INT DEFAULT 1 NOT NULL");
			db.execSQL("ALTER TABLE urzadzenia ADD COLUMN Tab_GPIO_SA INT DEFAULT 1 NOT NULL");
			db.execSQL("ALTER TABLE urzadzenia ADD COLUMN Tab_GPIO_History INT DEFAULT 1 NOT NULL");
			db.execSQL("CREATE TABLE 'errorLog' ('_id' INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE,'id_u' INTEGER,'data' TEXT,'timestamp' TIMESTAMP DEFAULT CURRENT_TIMESTAMP);");
		}
		if (oldVersion<3){
			db.execSQL("ALTER TABLE urzadzenia ADD COLUMN Tab_GPIO_Sensors INT DEFAULT 1 NOT NULL");
			db.execSQL("ALTER TABLE urzadzenia ADD COLUMN Tab_Notifications INT DEFAULT 1 NOT NULL");
            db.execSQL("ALTER TABLE urzadzenia ADD COLUMN selected_tab INTEGER NOT NULL DEFAULT 0");
			db.execSQL("ALTER TABLE urzadzenia ADD COLUMN Tab_GPIO_ASA INT DEFAULT 1 NOT NULL");
			db.execSQL("CREATE TABLE powiadomienia ("+
				"_id	INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE,"+
				"connection_id INTEGER NOT NULL,"+
				"target_id	TEXT NOT NULL,"+
				"type	TEXT NOT NULL,"+
				"value	TEXT NOT NULL,"+
				"condition	TEXT DEFAULT '==',"+
				"precise	INTEGER NOT NULL DEFAULT 0,"+
				"repeat_sec	INTEGER NOT NULL DEFAULT 900,"+
				"sound_file_url TEXT,"+
				"last_update TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP"+
			");"
			);
		}
		if(oldVersion<6)
			db.execSQL("ALTER TABLE urzadzenia ADD COLUMN Tab_GPIO_Chains INT DEFAULT 1 NOT NULL");
	}
 
	
	public Cursor dajUrzadzenia(){
		SQLiteDatabase db = getReadableDatabase();
		Cursor kursor = db.query("urzadzenia", null,null,null,null,null,null);
		return kursor;
	}

	public Cursor dajUrzadzenie(int id){
		SQLiteDatabase db = getReadableDatabase();
		Cursor kursor = db.query("urzadzenia", null,"_id = "+id,null,null,null,null);
		return kursor;
	}
	
	public void dodajUrzadzenie(String nazwa, String ip, int port, String haslosh, String haslomd, float artime, int Tab_GPIO_Output, int Tab_GPIO_Input, int Tab_GPIO_Pwm, int Tab_GPIO_SA, int Tab_GPIO_History, int Tab_Sensors, int Tab_Notifications, int Tab_GPIO_ASA, int Tab_GPIO_Chains){
		SQLiteDatabase db = getWritableDatabase();
		ContentValues wartosci = new ContentValues();
		wartosci.put("nazwa",nazwa);
		wartosci.put("ip",ip);
		wartosci.put("port",port);
		wartosci.put("haslo",haslosh);
		wartosci.put("haslo2",haslomd);
		wartosci.put("autorefreshtime",artime);
		wartosci.put("Tab_GPIO_Output",Tab_GPIO_Output);
		wartosci.put("Tab_GPIO_Input",Tab_GPIO_Input);
		wartosci.put("Tab_GPIO_Pwm",Tab_GPIO_Pwm);
		wartosci.put("Tab_GPIO_SA",Tab_GPIO_SA);
		wartosci.put("Tab_GPIO_ASA",Tab_GPIO_ASA);
		wartosci.put("Tab_GPIO_History",Tab_GPIO_History);
		wartosci.put("Tab_GPIO_Sensors",Tab_Sensors);
        wartosci.put("Tab_Notifications",Tab_Notifications);
		wartosci.put("Tab_GPIO_Chains",Tab_GPIO_Chains);
		db.insertOrThrow("urzadzenia", null, wartosci);
		
	}
	
	public void edytujUrzadzenie(int id, String nazwa,String ip, int port, String haslosh, String haslomd, float artime, int Tab_GPIO_Output, int Tab_GPIO_Input, int Tab_GPIO_Pwm, int Tab_GPIO_SA, int Tab_GPIO_History,int Tab_Sensors, int Tab_Notifications, int Tab_GPIO_ASA, int Tab_GPIO_Chains){
		SQLiteDatabase db = getWritableDatabase(); 
		ContentValues wartosci = new ContentValues();
		wartosci.put("nazwa",nazwa);
		wartosci.put("ip",ip);
		wartosci.put("port",port);
		wartosci.put("haslo",haslosh);
		wartosci.put("haslo2",haslomd);
		wartosci.put("autorefreshtime",artime);
		wartosci.put("Tab_GPIO_Output",Tab_GPIO_Output);
		wartosci.put("Tab_GPIO_Input",Tab_GPIO_Input);
		wartosci.put("Tab_GPIO_Pwm",Tab_GPIO_Pwm);
		wartosci.put("Tab_GPIO_SA",Tab_GPIO_SA);
		wartosci.put("Tab_GPIO_ASA",Tab_GPIO_ASA);
		wartosci.put("Tab_GPIO_History",Tab_GPIO_History);
		wartosci.put("Tab_GPIO_Sensors",Tab_Sensors);
        wartosci.put("Tab_Notifications",Tab_Notifications);
		wartosci.put("Tab_GPIO_Chains",Tab_GPIO_Chains);
        wartosci.put("selected_tab",0);
		db.update("urzadzenia",wartosci, "_id="+id, null);
	}
	public void edytujUrzadzenie(int id, String nazwa,String ip, int port, float artime, int Tab_GPIO_Output, int Tab_GPIO_Input, int Tab_GPIO_Pwm, int Tab_GPIO_SA, int Tab_GPIO_History,int Tab_Sensors , int Tab_Notifications, int Tab_GPIO_ASA, int Tab_GPIO_Chains){
		SQLiteDatabase db = getWritableDatabase();
		ContentValues wartosci = new ContentValues();
		wartosci.put("nazwa",nazwa);
		wartosci.put("ip",ip);
		wartosci.put("port",port);
		wartosci.put("autorefreshtime",artime);
		wartosci.put("Tab_GPIO_Output",Tab_GPIO_Output);
		wartosci.put("Tab_GPIO_Input",Tab_GPIO_Input);
		wartosci.put("Tab_GPIO_Pwm",Tab_GPIO_Pwm);
		wartosci.put("Tab_GPIO_SA",Tab_GPIO_SA);
		wartosci.put("Tab_GPIO_ASA",Tab_GPIO_ASA);
		wartosci.put("Tab_GPIO_History",Tab_GPIO_History);
		wartosci.put("Tab_GPIO_Sensors",Tab_Sensors);
        wartosci.put("Tab_Notifications",Tab_Notifications);
		wartosci.put("Tab_GPIO_Chains",Tab_GPIO_Chains);
        wartosci.put("selected_tab",0);
		db.update("urzadzenia",wartosci, "_id="+id, null);
	}
    public void edytujUrzadzenie(int id, int selectedPos){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues wartosci = new ContentValues();
        wartosci.put("selected_tab",selectedPos);
        db.update("urzadzenia",wartosci, "_id="+id, null);
    }
	public void usunUrzadzenie(int id){
		SQLiteDatabase db = getWritableDatabase();
		db.delete("urzadzenia", "_id="+id, null);
		db.delete("powiadomienia", "connection_id="+id, null);
	}

	public void dodajLog(int id_u, String data){
		SQLiteDatabase db = getWritableDatabase();
		ContentValues wartosci = new ContentValues();
		wartosci.put("id_u",id_u);
		wartosci.put("data",data);
		db.insertOrThrow("errorLog", null, wartosci);

	}

	public Cursor dajLogi(int id_u){
		SQLiteDatabase db = getReadableDatabase();
		String[] tableColumns = new String[] {
				"_id",
				"id_u",
				"data",
				"datetime(timestamp, 'localtime') as timestamp"
		};
		Cursor kursor = db.query("errorLog", tableColumns,"id_u = "+id_u,null,null,null,"timestamp DESC");
		return kursor;
	}

	public void czyscLogi(int id_u){
		SQLiteDatabase db = getWritableDatabase();
		db.delete("errorLog", "_id NOT IN (SELECT _id FROM errorLog WHERE id_u = "+id_u+" ORDER BY timestamp DESC LIMIT 10)", null);
	}

	public Cursor dajPowiadomienie(int id){
		SQLiteDatabase db = getReadableDatabase();
		Cursor kursor = db.rawQuery("SELECT * FROM powiadomienia p LEFT JOIN urzadzenia u ON p.connection_id = u._id WHERE p._id = "+id, null);
		return kursor;
	}

	public void edutyjPowiadomienie(int id){
		SQLiteDatabase db = getWritableDatabase();
		Cursor c = db.rawQuery("UPDATE powiadomienia SET last_update = datetime('now') WHERE _id="+id,null);
		c.moveToFirst();
		c.close();
	}
	public Cursor dajPowiadomienia(boolean activeOnly, int conn_id){
		SQLiteDatabase db = getReadableDatabase();
		Cursor kursor;
		if(activeOnly)kursor = db.rawQuery("SELECT * FROM powiadomienia p LEFT JOIN urzadzenia u ON p.connection_id = u._id WHERE p.precise > 0", null);
		else kursor = db.rawQuery("SELECT * FROM powiadomienia p LEFT JOIN urzadzenia u ON p.connection_id = u._id WHERE p.connection_id ="+conn_id, null);

		return kursor;
	}

	public int dodajPowiadomienie(int conn_id,String target_id, String type, String value,String cond, int precise, long repeat_sec, String sound){
		SQLiteDatabase db = getWritableDatabase();
		ContentValues wartosci = new ContentValues();
		wartosci.put("connection_id",conn_id);
		wartosci.put("target_id",target_id);
		wartosci.put("type",type);
		wartosci.put("value",value);
		wartosci.put("condition",cond);
		wartosci.put("precise",precise);
		wartosci.put("repeat_sec",repeat_sec);
		wartosci.put("sound_file_url",sound);
		db.insertOrThrow("powiadomienia", null, wartosci);
		Cursor kursor = db.query("powiadomienia", null,null,null,null,null,null);
		kursor.moveToLast();
		int lid = kursor.getInt(0);
		return lid;
	}
	public void edytujPowiadomienie(int id,int conn_id,String target_id, String type, String value,String cond, int precise, long repeat_sec, String sound){
		SQLiteDatabase db = getWritableDatabase();
		ContentValues wartosci = new ContentValues();
		wartosci.put("connection_id",conn_id);
		wartosci.put("target_id",target_id);
		wartosci.put("type",type);
		wartosci.put("value",value);
		wartosci.put("condition",cond);
		wartosci.put("precise",precise);
		wartosci.put("repeat_sec",repeat_sec);
		wartosci.put("sound_file_url",sound);
		db.update("powiadomienia",wartosci,"_id="+id,null);

	}
	public void usunPowiadomienie(int id){
		SQLiteDatabase db = getWritableDatabase();
		db.delete("powiadomienia", "_id="+id, null);
	}
	public void usunPowiadomieniePoTID(String id){
		SQLiteDatabase db = getWritableDatabase();
		db.delete("powiadomienia", "target_id="+id, null);
	}
	public void usunPowiadomieniePoCID(int id){
		SQLiteDatabase db = getWritableDatabase();
		db.delete("powiadomienia", "connection_id="+id, null);
	}
   
}
