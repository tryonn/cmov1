package pt.up.fe.cmov.operations;

import java.text.ParseException;

import org.json.JSONException;
import org.json.JSONObject;

import pt.up.fe.cmov.entities.Doctor;
import pt.up.fe.cmov.entities.Person;
import pt.up.fe.cmov.entities.Speciality;
import pt.up.fe.cmov.rest.JSONOperations;
import pt.up.fe.cmov.rest.RailsRestClient;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

public class DoctorOperations {
	
	public static final String DOCTOR_CONTROLER = "doctors";
	
	
	public static boolean createDoctor(Context context, Doctor doctor){
		
		try{	
			RailsRestClient.Post(DOCTOR_CONTROLER, JSONOperations.doctorToJSON(doctor));
		}catch(Exception e){
			e.printStackTrace();
			Log.w("NO Internet", "You don't have a internet connection");
		}
				
		ContentValues values = new ContentValues();
		
		if (doctor.getId() > 0) {
			values.put(Person.PERSON_ID, doctor.getId());
		}
		
		values.put(Person.PERSON_NAME,doctor.getName());
		values.put(Person.PERSON_USERNAME, doctor.getUsername());
		values.put(Doctor.DOCTOR_PHOTO, doctor.getPhoto());
		values.put(Doctor.DOCTOR_SPECIALITY, doctor.getSpeciality().getId());
		values.put(Person.PERSON_BIRTHDATE, JSONOperations.dbDateFormater.format(doctor.getBirthDate().getTime()));
		Uri uri = context.getContentResolver().insert(Doctor.CONTENT_URI, values);		
		
		return (uri != null);
	}
	
	public static boolean updateDoctor(Context context, Doctor doctor){
		
		try{			
			RailsRestClient.Put(DOCTOR_CONTROLER,Integer.toString(doctor.getId()), JSONOperations.doctorToJSON(doctor));
			
		}catch(Exception e){
			e.printStackTrace();
			Log.w("NO Internet", "You don't have a internet connection");
		}
		
		ContentValues values = new ContentValues();
		
		values.put(Person.PERSON_NAME,doctor.getId());
		values.put(Person.PERSON_NAME,doctor.getName());

		values.put(Person.PERSON_USERNAME, doctor.getUsername());
		values.put(Doctor.DOCTOR_PHOTO, doctor.getPhoto());
		values.put(Doctor.DOCTOR_SPECIALITY, doctor.getSpeciality().getId());
		//values.put(Person.PERSON_BIRTHDATE, doctor.getBirthDate().toString());
		Uri updateDoctorUri = ContentUris.withAppendedId(Doctor.CONTENT_URI, doctor.getId());
		context.getContentResolver().update(updateDoctorUri, values, null, null);
		
		return true;
	}
		
	public static boolean deleteDoctor(Context context, Doctor doctor){
		
		try{
			RailsRestClient.Delete(DOCTOR_CONTROLER, Integer.toString(doctor.getId()));
		}catch(Exception e){
			e.printStackTrace();
			Log.w("NO Internet", "You don't have a internet connection");
		}
		
		Uri deleteDoctorUri = ContentUris.withAppendedId(Doctor.CONTENT_URI, doctor.getId()); 
		try{
			context.getContentResolver().delete(deleteDoctorUri, null, null); 
			return true;
		}catch(Exception e){
			Log.wtf("DELETE", "Could not delete doctor");
			return false;
		}
	} 
	
	public static Doctor getRemoteServerDoctor(Context context, int id){
		JSONObject json = RailsRestClient.Get(DOCTOR_CONTROLER + "/" + Integer.toString(id));
		try {
			 return JSONOperations.JSONToDoctor(json);
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Doctor getDoctor(Context context, int id) {
				
		Uri queryDoctorUri = ContentUris.withAppendedId(Doctor.CONTENT_URI, id); 
		Cursor cDoctor = context.getContentResolver().query(queryDoctorUri, null, null, null, null); 
		Doctor d = null;
		while (cDoctor.moveToNext()) { 
			   //Date birthdate = cDoctor.getString(cDoctor.getColumnIndex(Doctor.PERSON_BIRTHDATE));
			   String name = cDoctor.getString(cDoctor.getColumnIndex(Doctor.PERSON_NAME));
			   String username = cDoctor.getString(cDoctor.getColumnIndex(Doctor.PERSON_USERNAME)); 
			   String photo = cDoctor.getString(cDoctor.getColumnIndex(Doctor.DOCTOR_PHOTO)); 
			   String specialityId = cDoctor.getString(cDoctor.getColumnIndex(Doctor.DOCTOR_SPECIALITY)); 
			   d = new Doctor(id,name,null,username,photo,
					   Speciality.Records.get(Integer.parseInt(specialityId)));
			} 
		cDoctor.close();
		return d;
		

	}
}