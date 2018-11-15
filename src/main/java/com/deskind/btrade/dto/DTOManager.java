package com.deskind.btrade.dto;

import java.util.ArrayList;
import java.util.List;

import com.deskind.btrade.entities.Trader;
import com.deskind.btrade.entities.TradingSystem;

public class DTOManager {
	public static List<BalanceDTO> getBalances(List<Trader> traders){
		
		List<BalanceDTO> balances = new ArrayList<BalanceDTO>();
        for(Trader trader : traders){
            balances.add(new BalanceDTO(trader.getToken(), trader.getBalance()));
        }
        return balances;
	}

	public static List<TraderDTO> getTraders(List<Trader> traders) {
		List<TraderDTO> tradersAsDTO = new ArrayList<>();
		
		for(Trader trader : traders){
            TraderDTO traderDTO = trader.toDTO();
            for(TradingSystem tradingSystem : trader.getTsList()){
                traderDTO.getTsListDTO().add(tradingSystem.toDTO());
            }
            tradersAsDTO.add(traderDTO);
        }
		
		return tradersAsDTO;
	}
	
}
