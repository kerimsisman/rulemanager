package com.akbank.chatbot.rulemanager.tmp;

import java.math.BigDecimal;
import java.util.Date;

import lombok.Data;

//TODO delete this object after hesapListesi service call
@Data
public class HesapListesiRecord {
	private String dovizCinsi;
	private String hesapTuru;
	private BigDecimal bakiye;
	private Date vadeBitisTarihi;
}
