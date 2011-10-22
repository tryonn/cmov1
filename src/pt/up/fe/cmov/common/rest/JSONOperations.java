package pt.up.fe.cmov.common.rest;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import pt.up.fe.cmov.common.entities.Doctor;
import pt.up.fe.cmov.common.entities.Patient;
import pt.up.fe.cmov.common.entities.Speciality;

public class JSONOperations {
	
	public static final DateFormat dbDateFormater = new SimpleDateFormat("yyyy-MM-dd");

	public static JSONObject doctorToJSON(Doctor doctor) throws JSONException{
		
		String birthday = dbDateFormater.format(doctor.getBirthDate().getTime());
		
		JSONObject json = new JSONObject();
		json.put("birthdate", birthday);
		json.put("name", doctor.getName());
		json.put("password_md5", "LOL");
		json.put("username", doctor.getUsername());
		json.put("photo", doctor.getPhoto());
		json.put("speciality_id", doctor.getSpeciality().getId());
		return json;
	}
	
	public static Doctor JSONToDoctor(JSONObject json) throws JSONException, ParseException{
				
		int id = json.getInt("id");
		Date birthday =	dbDateFormater.parse(json.getString("birthday"));
		String name = json.getString("name");
		String username = json.getString("username");
		String photo = json.getString("photo");
		Speciality spec = Speciality.Records.get(json.getInt("speciality_id"));
		return new Doctor(id,name,birthday,username,photo,spec);
	}
	
	public static JSONObject patientToJSON(Patient patient) throws JSONException{
		
		String birthday = dbDateFormater.format(patient.getBirthDate().getTime());
		
		JSONObject json = new JSONObject();
		json.put("birthdate", birthday);
		json.put("name", patient.getName());
		json.put("password_md5", "LOL");
		json.put("username", patient.getUsername());
		json.put("address", patient.getAddress());
		json.put("sex", patient.getSex());
		return json;
	}
	
	public static Patient JSONToPatient(JSONObject json) throws JSONException, ParseException{
				
		int id = json.getInt("id");
		Date birthday =	dbDateFormater.parse(json.getString("birthday"));
		String name = json.getString("name");
		String username = json.getString("username");
		String photo = json.getString("address");
		String sex = json.getString("sex");
		return new Patient(id,name,birthday,username,photo,sex);
	}
}
