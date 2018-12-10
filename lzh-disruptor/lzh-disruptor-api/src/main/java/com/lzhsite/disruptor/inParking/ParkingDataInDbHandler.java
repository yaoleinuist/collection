package com.lzhsite.disruptor.inParking;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.WorkHandler;

public class ParkingDataInDbHandler implements EventHandler<InParkingDataEvent>,WorkHandler<InParkingDataEvent>{

	   @Override
	   public void onEvent(InParkingDataEvent event) throws Exception {
	      long threadId = Thread.currentThread().getId();
	        String carLicense = event.getCarLicense();
	      System.out.println(String.format("Thread Id %s save %s into db ....",threadId,carLicense));
	   }

	   @Override
	   public void onEvent(InParkingDataEvent event, long sequence, boolean endOfBatch) throws Exception {
	      // TODO Auto-generated method stub
	      this.onEvent(event);  
	   }

	}
