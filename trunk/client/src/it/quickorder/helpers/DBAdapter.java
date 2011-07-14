package it.quickorder.helpers;

import it.quickorder.domain.Cliente;
import it.quickorder.domain.Prodotto;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBAdapter 
{
	private static final String DATABASE_NAME = "quickorder.db";
	private static final int DATABASE_VERSION = 1;
	private SQLiteDatabase db;
	@SuppressWarnings("unused")
	private final Context context;
	private DbHelper dbHelper;
	
	public DBAdapter(Context context) 
	{
		this.context = context;
		dbHelper = new DbHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	public DBAdapter open() throws SQLException 
	{
		db = dbHelper.getWritableDatabase();
		return this;
	}
	
	public void close() 
	{
		db.close();
	}
	
	public Cliente recuperaDatiCliente()
	{
		String query = "SELECT * FROM user";
		Cursor cursor = db.rawQuery(query, null);
		if (cursor.getCount() != 0) {
			cursor.moveToFirst();
			Cliente cliente = new Cliente();
			cliente.setNome(cursor.getString(cursor.getColumnIndex("nome")));
			cliente.setIMEI(cursor.getString(cursor.getColumnIndex("imei")));
			cliente.setCognome(cursor.getString(cursor.getColumnIndex("cognome")));
			cliente.setCodiceFiscale(cursor.getString(cursor.getColumnIndex("cf")));
			cliente.setEmail(cursor.getString(cursor.getColumnIndex("email")));
			cliente.setLuogoNascita(cursor.getString(cursor.getColumnIndex("luogoNascita")));
			cliente.setSesso(cursor.getString(cursor.getColumnIndex("sesso")).charAt(0));
			cliente.setAbilitato(cursor.getInt(cursor.getColumnIndex("abilitato")) == 1);
			String data = cursor.getString(cursor.getColumnIndex("dataNascita"));
			Date dataNascita = new Date();
			dataNascita.setYear(Integer.parseInt(data.substring(0, 4)) - 1900);
			dataNascita.setMonth(Integer.parseInt(data.substring(5, 7)));
			dataNascita.setDate(Integer.parseInt(data.substring(8, 10)));
			cliente.setDataNascita(dataNascita);
			cursor.close();
			return cliente;
		}
		cursor.close();
		return null;
	}
	
	public int getMaxVersioneProdotti()
	{
		String query = "SELECT MAX(versione) FROM prodotti";
		Cursor cursor = db.rawQuery(query, null);
		cursor.moveToFirst();
    	if (cursor.getCount() > 0)
    	{
    		int versione = cursor.getInt(0);
    		cursor.close();
    		return versione;
    	}
    	cursor.close();
    	return -1;
	}
	
	public void aggiungiProdotto(Prodotto p)
	{
		ContentValues values = new ContentValues();
		values.put("codice", p.getCodice());
		values.put("nome", p.getNome());
		values.put("descrizione", p.getDescrizione());
		values.put("prezzo", p.getPrezzo());
		values.put("versione", p.getVersione());
		values.put("tipologia", p.getTipologia()); 
		if (isProdottoPresente(p))
		{
			db.update("prodotti", values, "codice='" + p.getCodice() + "'",null);
		}
		else
		{
			db.insert("prodotti", null, values);
		}
	}
	
	public boolean isProdottoPresente(Prodotto p)
	{
		String q = "SELECT * FROM prodotti where codice='" + p.getCodice() + "'";
		Cursor c = db.rawQuery(q, null);
		boolean presente = c.getCount() > 0;
		c.close();
		return presente;
	}
	
	private static class DbHelper extends SQLiteOpenHelper 
	{
		public DbHelper(Context context, String name, CursorFactory factory, int version) 
		{
			super(context, name, factory, version);
		}
		
		@Override
		public void onCreate(SQLiteDatabase db) 
		{
			String sql = "";
			sql = sql + "CREATE TABLE user (";
			sql = sql + " imei TEXT PRIMARY KEY,";
			sql = sql + " nome TEXT NOT NULL,";
			sql = sql + " cognome TEXT NOT NULL,";
			sql = sql + " cf TEXT NOT NULL,";
			sql = sql + " email TEXT NOT NULL,";
			sql = sql + " dataNascita DATETIME NOT NULL,";
			sql = sql + " luogoNascita TEXT NOT NULL,";
			sql = sql + " sesso TEXT NOT NULL,";
			sql = sql + " abilitato INTEGER NOT NULL";
			sql = sql + ")";
			db.execSQL(sql);
			String sql2 = "";
			sql2 += "CREATE TABLE prodotti (";
			sql2 += "codice TEXT PRIMARY KEY,";
			sql2 += "nome TEXT NOT NULL,";
			sql2 += "tipologia INTEGER NOT NULL,";
			sql2 += "prezzo DOUBLE NOT NULL,";
			sql2 += "versione INTEGER NOT NULL,";
			sql2 += "descrizione TEXT NOT NULL";
			sql2 += ")";
			db.execSQL(sql2);
		}
		
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) 
		{
			Log.w("TaskDBAdapter", "Upgrading from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS prodotti");
			db.execSQL("DROP TABLE IF EXISTS user");
			onCreate(db);
		}
	}

	public void registraCliente(Cliente nuovoCliente) 
	{
		ContentValues values = new ContentValues();	
		values.put("imei", nuovoCliente.getIMEI());
		values.put("nome", nuovoCliente.getNome());
		values.put("cognome", nuovoCliente.getCognome());
		values.put("cf", nuovoCliente.getCodiceFiscale());
		values.put("email", nuovoCliente.getEmail());
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
		values.put("dataNascita", dateFormat.format(nuovoCliente.getDataNascita()));
		values.put("luogoNascita", nuovoCliente.getLuogoNascita());
		values.put("sesso", "" + nuovoCliente.getSesso());
		values.put("abilitato",1);
		db.insert("user", null, values);
	}
	
	public List<Prodotto> caricaDatiProdotti(int tipologia) 
	{
		ArrayList<Prodotto> listaProdotti = new ArrayList<Prodotto>();
		String selection = "tipologia = ?";
		String[] selectionArgs = { "" + tipologia };
		Cursor cursor = db.query("prodotti", null, selection, selectionArgs, null, null, "nome");
		while(cursor.moveToNext())
		{
			Prodotto p = new Prodotto();
			p.setCodice(cursor.getString(0));
			p.setNome(cursor.getString(1));
			p.setTipologia(tipologia);
			p.setPrezzo(Double.parseDouble(cursor.getString(3)));
			p.setDescrizione(cursor.getString(cursor.getColumnIndex("descrizione")));
			listaProdotti.add(p);
		}
		cursor.close();
		return listaProdotti;
	}
}


