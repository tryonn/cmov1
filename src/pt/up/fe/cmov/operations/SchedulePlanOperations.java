package pt.up.fe.cmov.operations;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pt.up.fe.cmov.entities.Schedule;
import pt.up.fe.cmov.entities.SchedulePlan;
import pt.up.fe.cmov.rest.JSONOperations;
import pt.up.fe.cmov.rest.RailsRestClient;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

public class SchedulePlanOperations {
	
	public static final String SCHEDULE_PLAN_CONTROLLER = "schedule_plans";
	public static final String SCHEDULE_CONTROLLER = "schedules";
	
	
	public static int createSchedulePlan(Context context, SchedulePlan schedulePlan, boolean isRemote){
		
		if (isRemote) {
			try{	
				JSONObject obj = RailsRestClient.Post(SCHEDULE_PLAN_CONTROLLER, JSONOperations.schedulePlanToJSON(schedulePlan));
				schedulePlan.setId(obj.getInt("id"));
			}catch(Exception e){
				e.printStackTrace();
				Log.w("NO Internet", "You don't have a internet connection");
				Toast.makeText(context, "No internet connection... Retry later", Toast.LENGTH_LONG).show();
			}
		}
		
		ContentValues values = new ContentValues();
		
		if (schedulePlan.getId() > 0) {
			values.put(SchedulePlan.SCHEDULE_PLAN_ID, schedulePlan.getId());
		}
		
		values.put(SchedulePlan.SCHEDULE_STARTDATE,JSONOperations.dbDateFormater.format(schedulePlan.getStartDate()));
		values.put(SchedulePlan.SCHEDULE_DOCTOR_ID, schedulePlan.getDoctorId());
		context.getContentResolver().insert(SchedulePlan.CONTENT_URI, values);		
		
		return schedulePlan.getId();
	}
	
	public static boolean updateSchedulePlan(Context context, SchedulePlan schedulePlan, boolean isRemote){
		if (isRemote) {
			try{			
				RailsRestClient.Put(SCHEDULE_PLAN_CONTROLLER,Integer.toString(schedulePlan.getId()), JSONOperations.schedulePlanToJSON(schedulePlan));
				
			}catch(Exception e){
				e.printStackTrace();
				Log.w("NO Internet", "You don't have a internet connection");
				Toast.makeText(context, "No internet connection... Retry later", Toast.LENGTH_LONG).show();
			}
		}
		
		ContentValues values = new ContentValues();
		
		values.put(SchedulePlan.SCHEDULE_PLAN_ID,schedulePlan.getId());
		values.put(SchedulePlan.SCHEDULE_STARTDATE,JSONOperations.dbDateFormater.format(schedulePlan.getStartDate()));
		values.put(SchedulePlan.SCHEDULE_DOCTOR_ID, schedulePlan.getDoctorId());
		Uri updateSchedulePlanUri = ContentUris.withAppendedId(SchedulePlan.CONTENT_URI, schedulePlan.getId());
		context.getContentResolver().update(updateSchedulePlanUri, values, null, null);
		
		return true;
	}
		
	public static boolean deleteSchedulePlan(Context context, SchedulePlan schedulePlan, boolean isRemote){
		if (isRemote) {
			try{
				RailsRestClient.Delete(SCHEDULE_PLAN_CONTROLLER, Integer.toString(schedulePlan.getId()));
			}catch(Exception e){
				e.printStackTrace();
				Log.w("NO Internet", "You don't have a internet connection");
				Toast.makeText(context, "No internet connection... Retry later", Toast.LENGTH_LONG).show();
			}
		}
		
		Uri deleteSchedulePlanUri = ContentUris.withAppendedId(SchedulePlan.CONTENT_URI, schedulePlan.getId()); 
		try{
			context.getContentResolver().delete(deleteSchedulePlanUri, null, null); 
			return true;
		}catch(Exception e){
			Log.wtf("DELETE", "Could not delete doctor");
			return false;
		}
	} 
	
	public static SchedulePlan getRemoteServerSchedulePlan(Context context, int id){
		try {	
			JSONObject json = RailsRestClient.Get(SCHEDULE_PLAN_CONTROLLER + "/" + Integer.toString(id));
			return JSONOperations.JSONToSchedulePlan(json);
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (ConnectTimeoutException e) {
		}
		return null;
	}
	
	public static SchedulePlan getSchedulePlan(Context context, int id) {
				
		Uri querySchedulePlanUri = ContentUris.withAppendedId(SchedulePlan.CONTENT_URI, id); 
		Cursor cSchedulePlan = context.getContentResolver().query(querySchedulePlanUri, null, null, null, null); 
		SchedulePlan sch = null;
		while (cSchedulePlan.moveToNext()) { 
			   Date dateTime = new Date(cSchedulePlan.getString(cSchedulePlan.getColumnIndex(SchedulePlan.SCHEDULE_STARTDATE)));
			   String doctor_id = cSchedulePlan.getString(cSchedulePlan.getColumnIndex(SchedulePlan.SCHEDULE_DOCTOR_ID)); 
			   sch = new SchedulePlan(id,Integer.parseInt(doctor_id),dateTime);
			} 
		cSchedulePlan.close();
		return sch;
	}
	
	public static ArrayList<Schedule> getRemoteSchedules(Context context, int schedulePlanId) {
		ArrayList<Schedule> schedules = new ArrayList<Schedule>();
		try {

			JSONArray jsonArray = RailsRestClient.GetArray(SCHEDULE_PLAN_CONTROLLER + "/" + Integer.toString(schedulePlanId) + "/" + SCHEDULE_CONTROLLER);
		
			for (int i=0; i < jsonArray.length(); i++) {
					JSONObject obj = jsonArray.getJSONObject(i);
					Schedule schedule = JSONOperations.JSONToSchedule(obj);
					schedules.add(schedule);
					Log.i("SCHEDULE", schedule.getStartDate().toString() + "---" + schedule.getEndDate().toString());
			}
		} catch (JSONException e) {
			Log.e("SCHEDULE_PLAN", "Couldn't decode JSON object");
		} catch (ParseException e) {
			Log.e("SCHEDULE_PLAN", "Couldn't decode JSON object");
		} catch (ConnectTimeoutException e) {
		}
		
		return schedules;
	}
	
	public static boolean createOrUpdateSchedulePlan(Context context, SchedulePlan plan) {
		if (getSchedulePlan(context, plan.getId()) == null) {
			createSchedulePlan(context, plan, false);
		}
		else {
			updateSchedulePlan(context, plan, false);
		}
		return true;
	}
	
	public static ArrayList<SchedulePlan> querySchedulePlans(Context context, String selection, String[] selectedArguments, String order){
        ArrayList<SchedulePlan> queryPlans = new ArrayList<SchedulePlan>();
        Cursor cSchedulePlan = context.getContentResolver().query(SchedulePlan.CONTENT_URI, null, selection, selectedArguments, order); 
        while (cSchedulePlan.moveToNext()) { 
        	int id = cSchedulePlan.getInt(cSchedulePlan.getColumnIndex(SchedulePlan.SCHEDULE_PLAN_ID)); 
            String dateStr = cSchedulePlan.getString(cSchedulePlan.getColumnIndex(SchedulePlan.SCHEDULE_STARTDATE));
    	 	String doctor_id = cSchedulePlan.getString(cSchedulePlan.getColumnIndex(SchedulePlan.SCHEDULE_DOCTOR_ID)); 
		   	
    	 	Date date = null;
    	 	try {
    	 		date = JSONOperations.dbDateFormater.parse(dateStr);
    	 	} catch (ParseException e) {
		    	 e.printStackTrace();
		     }
    	 	SchedulePlan sch = new SchedulePlan(id,Integer.parseInt(doctor_id),date); 
             
            queryPlans.add(sch);
         } 
        cSchedulePlan.close();
        return queryPlans;
     }
}
