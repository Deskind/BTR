package com.deskind.btrade.utils;

import java.lang.reflect.Type;
import java.util.Date;

import com.deskind.btrade.binary.objects.ProfitTableEntry;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class EntryDeserializer implements JsonDeserializer<ProfitTableEntry>{
	@Override
	public ProfitTableEntry deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
		ProfitTableEntry profitTableEntry = new ProfitTableEntry();
		
		JsonObject jsonObject = jsonElement.getAsJsonObject();
		
		profitTableEntry.setApp_id(jsonObject.get("app_id").getAsInt());
		profitTableEntry.setBuy_price(jsonObject.get("buy_price").getAsFloat());
		profitTableEntry.setContract_id(jsonObject.get("contract_id").getAsString());
		profitTableEntry.setLongcode(jsonObject.get("longcode").getAsString());
		profitTableEntry.setPayout(jsonObject.get("payout").getAsFloat());
		profitTableEntry.setPurchase_time(new Date(jsonObject.get("purchase_time").getAsLong()*1000));
		profitTableEntry.setSell_price(jsonObject.get("sell_price").getAsFloat());
		profitTableEntry.setSell_time(new Date(jsonObject.get("sell_time").getAsLong()*1000));
		profitTableEntry.setShortcode(jsonObject.get("shortcode").getAsString());
		profitTableEntry.setTransaction_id(jsonObject.get("transaction_id").getAsString());
		
		
		return profitTableEntry;
	}
}
