package com.sms.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.sms.dto.VagonDto;
import com.sms.model.VagonModel;
import com.sms.service.VagonService;
import com.sms.serviceImp.VagonServiceImp;



@Controller
public class VagonController {

	@Autowired
	private VagonService vagonService;

	//Yuklab olish uchun Malumot yigib beradi
	List<VagonModel> vagonsToDownload  = new ArrayList<>();
	List<Integer> vagonsToDownloadTables  = new ArrayList<>();

	@PreAuthorize(value = "hasAnyRole('DIRECTOR', 'SAM','HAV','ANDJ')")
	@GetMapping("/vagons/createExcel")
	public void pdfFile(HttpServletResponse response) throws IOException {
		vagonService.createPdf(vagonsToDownload,response);
	 }

	@PreAuthorize(value = "hasAnyRole('DIRECTOR', 'SAM','HAV','ANDJ')")
	@GetMapping("/vagons/createTableExcel")
	public void pdfTableFile(HttpServletResponse response) throws IOException {
		vagonService.pdfTableFile(vagonsToDownloadTables,response);
	 }
	
	// handler method to hundle list vagons add return mode and view
    @PreAuthorize(value = "hasAnyRole('DIRECTOR', 'SAM','HAV','ANDJ')")
	@GetMapping("/vagons")
	public String listVagon(Model model) {

		List<Integer> vagonsToDownloadTable  = new ArrayList<>();

		vagonsToDownload = vagonService.findAll();
		model.addAttribute("vagons", vagonService.findAll());

		//Kritiylarni  hammasini olish
		int samKritiy =  vagonService.getVagonsCount("Yopiq vagon (????????)","VCHD-6");
		int havKritiy =  vagonService.getVagonsCount("Yopiq vagon (????????)","VCHD-3");
		int andjKritiy =  vagonService.getVagonsCount("Yopiq vagon (????????)","VCHD-5");

		model.addAttribute("havKritiy", havKritiy);
		model.addAttribute("andjKritiy", andjKritiy);
		model.addAttribute("samKritiy",samKritiy);

		vagonsToDownloadTable.add(havKritiy);
		vagonsToDownloadTable.add(andjKritiy);
		vagonsToDownloadTable.add(samKritiy);
		vagonsToDownloadTable.add(samKritiy + havKritiy + andjKritiy);

		//Platformalarni  hammasini olish
		int samPlatforma =  vagonService.getVagonsCount("Platforma(????)","VCHD-6");
		int havPlatforma =  vagonService.getVagonsCount("Platforma(????)","VCHD-3");
		int andjPlatforma =  vagonService.getVagonsCount("Platforma(????)","VCHD-5");

		model.addAttribute("samPlatforma",samPlatforma);
		model.addAttribute("havPlatforma", havPlatforma);
		model.addAttribute("andjPlatforma", andjPlatforma);

		vagonsToDownloadTable.add(havPlatforma);
		vagonsToDownloadTable.add(andjPlatforma);
		vagonsToDownloadTable.add(samPlatforma);
		vagonsToDownloadTable.add(havPlatforma + andjPlatforma + samPlatforma);

		//Poluvagonlarni  hammasini olish
		int samPoluvagon =  vagonService.getVagonsCount("Yarim ochiq vagon(????)","VCHD-6");
		int havPoluvagon =  vagonService.getVagonsCount("Yarim ochiq vagon(????)","VCHD-3");
		int andjPoluvagon =  vagonService.getVagonsCount("Yarim ochiq vagon(????)","VCHD-5");

		model.addAttribute("samPoluvagon", samPoluvagon);
		model.addAttribute("havPoluvagon", havPoluvagon);
		model.addAttribute("andjPoluvagon", andjPoluvagon);

		vagonsToDownloadTable.add(havPoluvagon);
		vagonsToDownloadTable.add(andjPoluvagon);
		vagonsToDownloadTable.add(samPoluvagon);
		vagonsToDownloadTable.add(havPoluvagon + andjPoluvagon + samPoluvagon);

		//Tsisternalarni  hammasini olish
		int samTsisterna = vagonService.getVagonsCount("Sisterna(????)","VCHD-6");
		int havTsisterna = vagonService.getVagonsCount("Sisterna(????)","VCHD-3");
		int andjTsisterna = vagonService.getVagonsCount("Sisterna(????)","VCHD-5");

		model.addAttribute("samTsisterna", samTsisterna);
		model.addAttribute("havTsisterna", havTsisterna);
		model.addAttribute("andjTsisterna", andjTsisterna);

		vagonsToDownloadTable.add(havTsisterna);
		vagonsToDownloadTable.add(andjTsisterna);
		vagonsToDownloadTable.add(samTsisterna);
		vagonsToDownloadTable.add(havTsisterna + andjTsisterna + samTsisterna);

		//Boshqalarni  hammasini olish
		int samBoshqa = vagonService.getVagonsCount("Boshqa turdagi(????????)","VCHD-6");
		int havBoshqa = vagonService.getVagonsCount("Boshqa turdagi(????????)","VCHD-3");
		int andjBoshqa = vagonService.getVagonsCount("Boshqa turdagi(????????)","VCHD-5");

		model.addAttribute("samBoshqa", samBoshqa);
		model.addAttribute("havBoshqa", havBoshqa);
		model.addAttribute("andjBoshqa", andjBoshqa);

		vagonsToDownloadTable.add(havBoshqa);
		vagonsToDownloadTable.add(andjBoshqa);
		vagonsToDownloadTable.add(samBoshqa);
		vagonsToDownloadTable.add(havBoshqa + andjBoshqa + samBoshqa);

		// Jaminini olish
		int hammasi = vagonService.getCount("VCHD-6") + vagonService.getCount("VCHD-3") + vagonService.getCount("VCHD-5");
		int sam = vagonService.getCount("VCHD-6") ;
		int hav = vagonService.getCount("VCHD-3") ;
		int andj =  vagonService.getCount("VCHD-5");

		model.addAttribute("hammasi", hammasi);
		model.addAttribute("sam", sam);
		model.addAttribute("hav", hav);
		model.addAttribute("andj", andj);

		vagonsToDownloadTable.add(hav);
		vagonsToDownloadTable.add(andj);
		vagonsToDownloadTable.add(sam);
		vagonsToDownloadTable.add(hammasi);

//		Vaqtni olib turadi
		model.addAttribute("samDate",vagonService.getSamDate());
		model.addAttribute("havDate", vagonService.getHavDate());
		model.addAttribute("andjDate",vagonService.getAndjDate());

		vagonsToDownloadTables = vagonsToDownloadTable;
		return "vagons";
	}

    //yangi vagon qoshish uchun oyna
    @PreAuthorize(value = "hasAnyRole('DIRECTOR', 'SAM','HAV','ANDJ')")
	@GetMapping("/vagons/new")
	public String createVagonForm(Model model) {
		VagonDto vagonDto = new VagonDto();
		model.addAttribute("vagon", vagonDto);
		return "create_vagon";
	}
    
    //vagon qoshish
    @PreAuthorize(value = "hasAnyRole('DIRECTOR', 'SAM','HAV','ANDJ')")
	@PostMapping("/vagons")
	public String saveVagon(@ModelAttribute("vagon") VagonDto vagon, HttpServletRequest request ) { 
    	if (request.isUserInRole("DIRECTOR")) {
    		vagonService.saveVagon(vagon);
        }else if (request.isUserInRole("SAM")) {
        		vagonService.saveVagonSam(vagon);
        }else if (request.isUserInRole("HAV")) {
    		vagonService.saveVagonHav(vagon);
        }else if (request.isUserInRole("ANDJ")) {
    		vagonService.saveVagonAndj(vagon);
        }
		return "redirect:/vagons";
	}
    
    //tahrirlash uchun oyna
    @PreAuthorize(value = "hasAnyRole('DIRECTOR', 'SAM','HAV','ANDJ')")
	@GetMapping("/vagons/edit/{id}")
	public String editVagonForm(@PathVariable("id") Long id, Model model) {
		model.addAttribute("vagon",vagonService.getVagonById(id));
		return "edit_vagon";
	}

    //tahrirni saqlash
    @PreAuthorize(value = "hasAnyRole('DIRECTOR', 'SAM','HAV','ANDJ')")
	@PostMapping("/vagons/{id}")
	public String updateVagon(@ModelAttribute("vagon") VagonDto vagon,@PathVariable Long id,Model model, HttpServletRequest request) throws NotFoundException {
    	if (request.isUserInRole("DIRECTOR")) {
    		vagonService.updateVagon(vagon, id);
        }else if (request.isUserInRole("SAM")) {
        	vagonService.updateVagonSam(vagon, id);
        }else if (request.isUserInRole("HAV")) {
        	vagonService.updateVagonHav(vagon, id);
        }else if (request.isUserInRole("ANDJ")) {
        	vagonService.updateVagonAndj(vagon, id);
        }
		return "redirect:/vagons";
	}
	
    //bazadan o'chirish
    @PreAuthorize(value = "hasAnyRole('DIRECTOR', 'SAM','HAV','ANDJ')")
	@GetMapping("/vagons/delete/{id}")
	public String deleteVagonForm(@PathVariable("id") Long id, HttpServletRequest request ) throws NotFoundException {
    	if (request.isUserInRole("DIRECTOR")) {
    		vagonService.deleteVagonById(id);
        }else if (request.isUserInRole("SAM")) {
        		vagonService.deleteVagonSam(id);
        }else if (request.isUserInRole("HAV")) {
    		vagonService.deleteVagonHav(id);
        }else if (request.isUserInRole("ANDJ")) {
    		vagonService.deleteVagonAndj(id);
        }
		return "redirect:/vagons";
	}
	
    // wagon nomer orqali qidirish
    @PreAuthorize(value = "hasAnyRole('DIRECTOR', 'SAM','HAV','ANDJ')")
	@GetMapping("/vagons/search")
	public String searchByNomer(Model model,  @RequestParam(value = "participant", required = false) Integer participant) {
		if(participant == null  ) {
			model.addAttribute("vagons", vagonService.findAll());	
			vagonsToDownload = vagonService.findAll();
		}else {
			model.addAttribute("vagons", vagonService.findByKeyword(participant));
			List<VagonModel> emptyList = new ArrayList<>();
			vagonsToDownload=emptyList;
			vagonsToDownload.add(vagonService.findByKeyword(participant)) ;
		}

		List<Integer> vagonsToDownloadTable  = new ArrayList<>();

		//Kritiylarni  hammasini olish
		int samKritiy =  vagonService.getVagonsCount("Yopiq vagon (????????)","VCHD-6");
		int havKritiy =  vagonService.getVagonsCount("Yopiq vagon (????????)","VCHD-3");
		int andjKritiy =  vagonService.getVagonsCount("Yopiq vagon (????????)","VCHD-5");

		model.addAttribute("havKritiy", havKritiy);
		model.addAttribute("andjKritiy", andjKritiy);
		model.addAttribute("samKritiy",samKritiy);

		vagonsToDownloadTable.add(havKritiy);
		vagonsToDownloadTable.add(andjKritiy);
		vagonsToDownloadTable.add(samKritiy);
		vagonsToDownloadTable.add(samKritiy + havKritiy + andjKritiy);

		//Platformalarni  hammasini olish
		int samPlatforma =  vagonService.getVagonsCount("Platforma(????)","VCHD-6");
		int havPlatforma =  vagonService.getVagonsCount("Platforma(????)","VCHD-3");
		int andjPlatforma =  vagonService.getVagonsCount("Platforma(????)","VCHD-5");

		model.addAttribute("samPlatforma",samPlatforma);
		model.addAttribute("havPlatforma", havPlatforma);
		model.addAttribute("andjPlatforma", andjPlatforma);

		vagonsToDownloadTable.add(havPlatforma);
		vagonsToDownloadTable.add(andjPlatforma);
		vagonsToDownloadTable.add(samPlatforma);
		vagonsToDownloadTable.add(havPlatforma + andjPlatforma + samPlatforma);

		//Poluvagonlarni  hammasini olish
		int samPoluvagon =  vagonService.getVagonsCount("Yarim ochiq vagon(????)","VCHD-6");
		int havPoluvagon =  vagonService.getVagonsCount("Yarim ochiq vagon(????)","VCHD-3");
		int andjPoluvagon =  vagonService.getVagonsCount("Yarim ochiq vagon(????)","VCHD-5");

		model.addAttribute("samPoluvagon", samPoluvagon);
		model.addAttribute("havPoluvagon", havPoluvagon);
		model.addAttribute("andjPoluvagon", andjPoluvagon);

		vagonsToDownloadTable.add(havPoluvagon);
		vagonsToDownloadTable.add(andjPoluvagon);
		vagonsToDownloadTable.add(samPoluvagon);
		vagonsToDownloadTable.add(havPoluvagon + andjPoluvagon + samPoluvagon);

		//Tsisternalarni  hammasini olish
		int samTsisterna = vagonService.getVagonsCount("Sisterna(????)","VCHD-6");
		int havTsisterna = vagonService.getVagonsCount("Sisterna(????)","VCHD-3");
		int andjTsisterna = vagonService.getVagonsCount("Sisterna(????)","VCHD-5");

		model.addAttribute("samTsisterna", samTsisterna);
		model.addAttribute("havTsisterna", havTsisterna);
		model.addAttribute("andjTsisterna", andjTsisterna);

		vagonsToDownloadTable.add(havTsisterna);
		vagonsToDownloadTable.add(andjTsisterna);
		vagonsToDownloadTable.add(samTsisterna);
		vagonsToDownloadTable.add(havTsisterna + andjTsisterna + samTsisterna);

		//Boshqalarni  hammasini olish
		int samBoshqa = vagonService.getVagonsCount("Boshqa turdagi(????????)","VCHD-6");
		int havBoshqa = vagonService.getVagonsCount("Boshqa turdagi(????????)","VCHD-3");
		int andjBoshqa = vagonService.getVagonsCount("Boshqa turdagi(????????)","VCHD-5");

		model.addAttribute("samBoshqa", samBoshqa);
		model.addAttribute("havBoshqa", havBoshqa);
		model.addAttribute("andjBoshqa", andjBoshqa);

		vagonsToDownloadTable.add(havBoshqa);
		vagonsToDownloadTable.add(andjBoshqa);
		vagonsToDownloadTable.add(samBoshqa);
		vagonsToDownloadTable.add(havBoshqa + andjBoshqa + samBoshqa);

		// Jaminini olish
		int hammasi = vagonService.getCount("VCHD-6") + vagonService.getCount("VCHD-3") + vagonService.getCount("VCHD-5");
		int sam = vagonService.getCount("VCHD-6") ;
		int hav = vagonService.getCount("VCHD-3") ;
		int andj =  vagonService.getCount("VCHD-5");

		model.addAttribute("hammasi", hammasi);
		model.addAttribute("sam", sam);
		model.addAttribute("hav", hav);
		model.addAttribute("andj", andj);

		vagonsToDownloadTable.add(hav);
		vagonsToDownloadTable.add(andj);
		vagonsToDownloadTable.add(sam);
		vagonsToDownloadTable.add(hammasi);

//		Vaqtni olib turadi
		model.addAttribute("samDate",vagonService.getSamDate());
		model.addAttribute("havDate", vagonService.getHavDate());
		model.addAttribute("andjDate",vagonService.getAndjDate());

		vagonsToDownloadTables = vagonsToDownloadTable;
		
		return "vagons";		
	}
	
    //Filter qilish
    @PreAuthorize(value = "hasAnyRole('DIRECTOR', 'SAM','HAV','ANDJ')")
	@GetMapping("/vagons/filter")
	public String filterByDepoNomi(Model model,  @RequestParam(value = "depoNomi", required = false) String depoNomi,
												@RequestParam(value = "vagonTuri", required = false) String vagonTuri,
												@RequestParam(value = "country", required = false) String country) {
		if(!depoNomi.equalsIgnoreCase("Hammasi") && !vagonTuri.equalsIgnoreCase("Hammasi") && !country.equalsIgnoreCase("Hammasi") ) {
			model.addAttribute("vagons", vagonService.findAllByDepoNomiVagonTuriAndCountry(depoNomi, vagonTuri, country));
			vagonsToDownload=vagonService.findAllByDepoNomiVagonTuriAndCountry(depoNomi, vagonTuri, country);
		}else if(depoNomi.equalsIgnoreCase("Hammasi") && !vagonTuri.equalsIgnoreCase("Hammasi") && !country.equalsIgnoreCase("Hammasi")){
			model.addAttribute("vagons", vagonService.findAllByVagonTuriAndCountry(vagonTuri , country));
			vagonsToDownload=vagonService.findAllByVagonTuriAndCountry(vagonTuri , country);
		}else if(depoNomi.equalsIgnoreCase("Hammasi") && vagonTuri.equalsIgnoreCase("Hammasi")&& !country.equalsIgnoreCase("Hammasi")){
			model.addAttribute("vagons", vagonService.findAllBycountry(country ));
			vagonsToDownload=vagonService.findAllBycountry(country );
		}else if(!depoNomi.equalsIgnoreCase("Hammasi") && !vagonTuri.equalsIgnoreCase("Hammasi") && country.equalsIgnoreCase("Hammasi")){
			model.addAttribute("vagons", vagonService.findAllByDepoNomiAndVagonTuri(depoNomi, vagonTuri));
			vagonsToDownload=vagonService.findAllByDepoNomiAndVagonTuri(depoNomi, vagonTuri);
		}else if(!depoNomi.equalsIgnoreCase("Hammasi") && vagonTuri.equalsIgnoreCase("Hammasi") && !country.equalsIgnoreCase("Hammasi")){
			model.addAttribute("vagons", vagonService.findAllByDepoNomiAndCountry(depoNomi, country));
			vagonsToDownload=vagonService.findAllByDepoNomiAndCountry(depoNomi, country);
		}else if(depoNomi.equalsIgnoreCase("Hammasi") && !vagonTuri.equalsIgnoreCase("Hammasi") && country.equalsIgnoreCase("Hammasi")){
			model.addAttribute("vagons", vagonService.findAllByVagonTuri(vagonTuri));
			vagonsToDownload=vagonService.findAllByVagonTuri(vagonTuri);
		}else if(!depoNomi.equalsIgnoreCase("Hammasi") && vagonTuri.equalsIgnoreCase("Hammasi") && country.equalsIgnoreCase("Hammasi")){
			model.addAttribute("vagons", vagonService.findAllByDepoNomi(depoNomi ));
			vagonsToDownload=vagonService.findAllByDepoNomi(depoNomi );
		}else {
			model.addAttribute("vagons", vagonService.findAll());
			vagonsToDownload=vagonService.findAll();
		}

		//		Vaqtni olib turadi
		model.addAttribute("samDate",vagonService.getSamDate());
		model.addAttribute("havDate", vagonService.getHavDate());
		model.addAttribute("andjDate",vagonService.getAndjDate());
		
		if (country.equalsIgnoreCase("Hammasi")) {
			List<Integer> vagonsToDownloadTable  = new ArrayList<>();
			
			//Kritiylarni  hammasini olish
			int samKritiy =  vagonService.getVagonsCount("Yopiq vagon (????????)","VCHD-6");
			int havKritiy =  vagonService.getVagonsCount("Yopiq vagon (????????)","VCHD-3");
			int andjKritiy =  vagonService.getVagonsCount("Yopiq vagon (????????)","VCHD-5");

			model.addAttribute("havKritiy", havKritiy);
			model.addAttribute("andjKritiy", andjKritiy);
			model.addAttribute("samKritiy",samKritiy);

			vagonsToDownloadTable.add(havKritiy);
			vagonsToDownloadTable.add(andjKritiy);
			vagonsToDownloadTable.add(samKritiy);
			vagonsToDownloadTable.add(samKritiy + havKritiy + andjKritiy);

			//Platformalarni  hammasini olish
			int samPlatforma =  vagonService.getVagonsCount("Platforma(????)","VCHD-6");
			int havPlatforma =  vagonService.getVagonsCount("Platforma(????)","VCHD-3");
			int andjPlatforma =  vagonService.getVagonsCount("Platforma(????)","VCHD-5");

			model.addAttribute("samPlatforma",samPlatforma);
			model.addAttribute("havPlatforma", havPlatforma);
			model.addAttribute("andjPlatforma", andjPlatforma);

			vagonsToDownloadTable.add(havPlatforma);
			vagonsToDownloadTable.add(andjPlatforma);
			vagonsToDownloadTable.add(samPlatforma);
			vagonsToDownloadTable.add(havPlatforma + andjPlatforma + samPlatforma);

			//Poluvagonlarni  hammasini olish
			int samPoluvagon =  vagonService.getVagonsCount("Yarim ochiq vagon(????)","VCHD-6");
			int havPoluvagon =  vagonService.getVagonsCount("Yarim ochiq vagon(????)","VCHD-3");
			int andjPoluvagon =  vagonService.getVagonsCount("Yarim ochiq vagon(????)","VCHD-5");

			model.addAttribute("samPoluvagon", samPoluvagon);
			model.addAttribute("havPoluvagon", havPoluvagon);
			model.addAttribute("andjPoluvagon", andjPoluvagon);

			vagonsToDownloadTable.add(havPoluvagon);
			vagonsToDownloadTable.add(andjPoluvagon);
			vagonsToDownloadTable.add(samPoluvagon);
			vagonsToDownloadTable.add(havPoluvagon + andjPoluvagon + samPoluvagon);

			//Tsisternalarni  hammasini olish
			int samTsisterna = vagonService.getVagonsCount("Sisterna(????)","VCHD-6");
			int havTsisterna = vagonService.getVagonsCount("Sisterna(????)","VCHD-3");
			int andjTsisterna = vagonService.getVagonsCount("Sisterna(????)","VCHD-5");

			model.addAttribute("samTsisterna", samTsisterna);
			model.addAttribute("havTsisterna", havTsisterna);
			model.addAttribute("andjTsisterna", andjTsisterna);

			vagonsToDownloadTable.add(havTsisterna);
			vagonsToDownloadTable.add(andjTsisterna);
			vagonsToDownloadTable.add(samTsisterna);
			vagonsToDownloadTable.add(havTsisterna + andjTsisterna + samTsisterna);

			//Boshqalarni  hammasini olish
			int samBoshqa = vagonService.getVagonsCount("Boshqa turdagi(????????)","VCHD-6");
			int havBoshqa = vagonService.getVagonsCount("Boshqa turdagi(????????)","VCHD-3");
			int andjBoshqa = vagonService.getVagonsCount("Boshqa turdagi(????????)","VCHD-5");

			model.addAttribute("samBoshqa", samBoshqa);
			model.addAttribute("havBoshqa", havBoshqa);
			model.addAttribute("andjBoshqa", andjBoshqa);

			vagonsToDownloadTable.add(havBoshqa);
			vagonsToDownloadTable.add(andjBoshqa);
			vagonsToDownloadTable.add(samBoshqa);
			vagonsToDownloadTable.add(havBoshqa + andjBoshqa + samBoshqa);

			// Jaminini olish
			int hammasi = vagonService.getCount("VCHD-6") + vagonService.getCount("VCHD-3") + vagonService.getCount("VCHD-5");
			int sam = vagonService.getCount("VCHD-6") ;
			int hav = vagonService.getCount("VCHD-3") ;
			int andj =  vagonService.getCount("VCHD-5");

			model.addAttribute("hammasi", hammasi);
			model.addAttribute("sam", sam);
			model.addAttribute("hav", hav);
			model.addAttribute("andj", andj);

			vagonsToDownloadTable.add(hav);
			vagonsToDownloadTable.add(andj);
			vagonsToDownloadTable.add(sam);
			vagonsToDownloadTable.add(hammasi);

			vagonsToDownloadTables = vagonsToDownloadTable;

		} else if (country.equalsIgnoreCase("O'TY(????????)")) {
			List<Integer> vagonsToDownloadTable  = new ArrayList<>();

			//Kritiylarni  hammasini olish
			int havKritiy = vagonService.getVagonsCount("Yopiq vagon (????????)", "VCHD-3","O'TY(????????)");
			int andjKritiy = vagonService.getVagonsCount("Yopiq vagon (????????)", "VCHD-5","O'TY(????????)");
			int samKritiy = vagonService.getVagonsCount("Yopiq vagon (????????)", "VCHD-6","O'TY(????????)");

			model.addAttribute("samKritiy", samKritiy);
			model.addAttribute("havKritiy", havKritiy);
			model.addAttribute("andjKritiy", andjKritiy);

			vagonsToDownloadTable.add(havKritiy);
			vagonsToDownloadTable.add(andjKritiy);
			vagonsToDownloadTable.add(samKritiy);
			vagonsToDownloadTable.add(havKritiy + andjKritiy + samKritiy);

			//Platformalarni  hammasini olish
			int samPlatforma =  vagonService.getVagonsCount("Platforma(????)", "VCHD-6","O'TY(????????)");
			int havPlatforma =  vagonService.getVagonsCount("Platforma(????)", "VCHD-3","O'TY(????????)");
			int andjPlatforma =  vagonService.getVagonsCount("Platforma(????)", "VCHD-5","O'TY(????????)");

			model.addAttribute("samPlatforma", samPlatforma);
			model.addAttribute("havPlatforma", havPlatforma);
			model.addAttribute("andjPlatforma", andjPlatforma);

			vagonsToDownloadTable.add(havPlatforma);
			vagonsToDownloadTable.add(andjPlatforma);
			vagonsToDownloadTable.add(samPlatforma);
			vagonsToDownloadTable.add(samPlatforma + andjPlatforma + havPlatforma);

			//Poluvagonlarni  hammasini olish
			int samPoluvagon = vagonService.getVagonsCount("Yarim ochiq vagon(????)", "VCHD-6","O'TY(????????)");
			int havPoluvagon = vagonService.getVagonsCount("Yarim ochiq vagon(????)", "VCHD-3","O'TY(????????)");
			int andjPoluvagon = vagonService.getVagonsCount("Yarim ochiq vagon(????)", "VCHD-5","O'TY(????????)");

			model.addAttribute("samPoluvagon", samPoluvagon);
			model.addAttribute("havPoluvagon", havPoluvagon);
			model.addAttribute("andjPoluvagon", andjPoluvagon);

			vagonsToDownloadTable.add(havPoluvagon);
			vagonsToDownloadTable.add(andjPoluvagon);
			vagonsToDownloadTable.add(samPoluvagon);
			vagonsToDownloadTable.add(samPoluvagon + havPoluvagon + andjPoluvagon);

			//Tsisternalarni  hammasini olish
			int samTsisterna = vagonService.getVagonsCount("Sisterna(????)", "VCHD-6","O'TY(????????)");
			int havTsisterna = vagonService.getVagonsCount("Sisterna(????)", "VCHD-3","O'TY(????????)");
			int andjTsisterna = vagonService.getVagonsCount("Sisterna(????)", "VCHD-5","O'TY(????????)");

			model.addAttribute("samTsisterna", samTsisterna);
			model.addAttribute("havTsisterna", havTsisterna);
			model.addAttribute("andjTsisterna", andjTsisterna);

			vagonsToDownloadTable.add(havTsisterna);
			vagonsToDownloadTable.add(andjTsisterna);
			vagonsToDownloadTable.add(samTsisterna);
			vagonsToDownloadTable.add(samTsisterna + andjTsisterna + havTsisterna);

			//Boshqalarni  hammasini olish
			int samBoshqa = vagonService.getVagonsCount("Boshqa turdagi(????????)", "VCHD-6","O'TY(????????)");
			int havBoshqa = vagonService.getVagonsCount("Boshqa turdagi(????????)", "VCHD-3","O'TY(????????)");
			int andjBoshqa = vagonService.getVagonsCount("Boshqa turdagi(????????)", "VCHD-5","O'TY(????????)");

			model.addAttribute("samBoshqa", samBoshqa);
			model.addAttribute("havBoshqa", havBoshqa);
			model.addAttribute("andjBoshqa", andjBoshqa);

			vagonsToDownloadTable.add(havBoshqa);
			vagonsToDownloadTable.add(andjBoshqa);
			vagonsToDownloadTable.add(samBoshqa);
			vagonsToDownloadTable.add(samBoshqa + andjBoshqa + havBoshqa);

			// Jaminini olish
			int hammasi = vagonService.getCount("VCHD-6","O'TY(????????)") + vagonService.getCount("VCHD-3","O'TY(????????)") + vagonService.getCount("VCHD-5","O'TY(????????)");
			int sam = vagonService.getCount("VCHD-6","O'TY(????????)");
			int hav = vagonService.getCount("VCHD-3","O'TY(????????)");
			int andj = vagonService.getCount("VCHD-5","O'TY(????????)");

			model.addAttribute("hammasi", hammasi);
			model.addAttribute("sam", sam);
			model.addAttribute("hav", hav);
			model.addAttribute("andj", andj);

			vagonsToDownloadTable.add(hav);
			vagonsToDownloadTable.add(andj);
			vagonsToDownloadTable.add(sam);
			vagonsToDownloadTable.add(sam + hav + andj);
			
			vagonsToDownloadTables = vagonsToDownloadTable;
			
		} else if (country.equalsIgnoreCase("MDH(??????)")) {
			List<Integer> vagonsToDownloadTable  = new ArrayList<>();

			//Kritiylarni  hammasini olish
			int havKritiy = vagonService.getVagonsCount("Yopiq vagon (????????)", "VCHD-3","MDH(??????)");
			int andjKritiy = vagonService.getVagonsCount("Yopiq vagon (????????)", "VCHD-5","MDH(??????)");
			int samKritiy = vagonService.getVagonsCount("Yopiq vagon (????????)", "VCHD-6","MDH(??????)");

			model.addAttribute("samKritiy", samKritiy);
			model.addAttribute("havKritiy", havKritiy);
			model.addAttribute("andjKritiy", andjKritiy);

			vagonsToDownloadTable.add(havKritiy);
			vagonsToDownloadTable.add(andjKritiy);
			vagonsToDownloadTable.add(samKritiy);
			vagonsToDownloadTable.add(havKritiy + andjKritiy + samKritiy);

			//Platformalarni  hammasini olish
			int samPlatforma =  vagonService.getVagonsCount("Platforma(????)", "VCHD-6","MDH(??????)");
			int havPlatforma =  vagonService.getVagonsCount("Platforma(????)", "VCHD-3","MDH(??????)");
			int andjPlatforma =  vagonService.getVagonsCount("Platforma(????)", "VCHD-5","MDH(??????)");

			model.addAttribute("samPlatforma", samPlatforma);
			model.addAttribute("havPlatforma", havPlatforma);
			model.addAttribute("andjPlatforma", andjPlatforma);

			vagonsToDownloadTable.add(havPlatforma);
			vagonsToDownloadTable.add(andjPlatforma);
			vagonsToDownloadTable.add(samPlatforma);
			vagonsToDownloadTable.add(samPlatforma + andjPlatforma + havPlatforma);

			//Poluvagonlarni  hammasini olish
			int samPoluvagon = vagonService.getVagonsCount("Yarim ochiq vagon(????)", "VCHD-6","MDH(??????)");
			int havPoluvagon = vagonService.getVagonsCount("Yarim ochiq vagon(????)", "VCHD-3","MDH(??????)");
			int andjPoluvagon = vagonService.getVagonsCount("Yarim ochiq vagon(????)", "VCHD-5","MDH(??????)");

			model.addAttribute("samPoluvagon", samPoluvagon);
			model.addAttribute("havPoluvagon", havPoluvagon);
			model.addAttribute("andjPoluvagon", andjPoluvagon);

			vagonsToDownloadTable.add(havPoluvagon);
			vagonsToDownloadTable.add(andjPoluvagon);
			vagonsToDownloadTable.add(samPoluvagon);
			vagonsToDownloadTable.add(samPoluvagon + havPoluvagon + andjPoluvagon);

			//Tsisternalarni  hammasini olish
			int samTsisterna = vagonService.getVagonsCount("Sisterna(????)", "VCHD-6","MDH(??????)");
			int havTsisterna = vagonService.getVagonsCount("Sisterna(????)", "VCHD-3","MDH(??????)");
			int andjTsisterna = vagonService.getVagonsCount("Sisterna(????)", "VCHD-5","MDH(??????)");

			model.addAttribute("samTsisterna", samTsisterna);
			model.addAttribute("havTsisterna", havTsisterna);
			model.addAttribute("andjTsisterna", andjTsisterna);

			vagonsToDownloadTable.add(havTsisterna);
			vagonsToDownloadTable.add(andjTsisterna);
			vagonsToDownloadTable.add(samTsisterna);
			vagonsToDownloadTable.add(samTsisterna + andjTsisterna + havTsisterna);

			//Boshqalarni  hammasini olish
			int samBoshqa = vagonService.getVagonsCount("Boshqa turdagi(????????)", "VCHD-6","MDH(??????)");
			int havBoshqa = vagonService.getVagonsCount("Boshqa turdagi(????????)", "VCHD-3","MDH(??????)");
			int andjBoshqa = vagonService.getVagonsCount("Boshqa turdagi(????????)", "VCHD-5","MDH(??????)");

			model.addAttribute("samBoshqa", samBoshqa);
			model.addAttribute("havBoshqa", havBoshqa);
			model.addAttribute("andjBoshqa", andjBoshqa);

			vagonsToDownloadTable.add(havBoshqa);
			vagonsToDownloadTable.add(andjBoshqa);
			vagonsToDownloadTable.add(samBoshqa);
			vagonsToDownloadTable.add(samBoshqa + andjBoshqa + havBoshqa);

			// Jaminini olish
			int hammasi = vagonService.getCount("VCHD-6","MDH(??????)") + vagonService.getCount("VCHD-3","MDH(??????)") + vagonService.getCount("VCHD-5","MDH(??????)");
			int sam = vagonService.getCount("VCHD-6","MDH(??????)");
			int hav = vagonService.getCount("VCHD-3","MDH(??????)");
			int andj = vagonService.getCount("VCHD-5","MDH(??????)");

			model.addAttribute("hammasi", hammasi);
			model.addAttribute("sam", sam);
			model.addAttribute("hav", hav);
			model.addAttribute("andj", andj);

			vagonsToDownloadTable.add(hav);
			vagonsToDownloadTable.add(andj);
			vagonsToDownloadTable.add(sam);
			vagonsToDownloadTable.add(sam + hav + andj);

			vagonsToDownloadTables = vagonsToDownloadTable;

//			//Kritiylarni  hammasini olish
//			model.addAttribute("samKritiy", vagonService.getVagonsCount("Yopiq vagon (????????)", "VCHD-6","MDH(??????)"));
//			model.addAttribute("havKritiy", vagonService.getVagonsCount("Yopiq vagon (????????)", "VCHD-3","MDH(??????)"));
//			model.addAttribute("andjKritiy", vagonService.getVagonsCount("Yopiq vagon (????????)", "VCHD-5","MDH(??????)"));
//
//			//Platformalarni  hammasini olish
//			model.addAttribute("samPlatforma", vagonService.getVagonsCount("Platforma(????)", "VCHD-6","MDH(??????)"));
//			model.addAttribute("havPlatforma", vagonService.getVagonsCount("Platforma(????)", "VCHD-3","MDH(??????)"));
//			model.addAttribute("andjPlatforma", vagonService.getVagonsCount("Platforma(????)", "VCHD-5","MDH(??????)"));
//
//			//Poluvagonlarni  hammasini olish
//			model.addAttribute("samPoluvagon", vagonService.getVagonsCount("Yarim ochiq vagon(????)", "VCHD-6","MDH(??????)"));
//			model.addAttribute("havPoluvagon", vagonService.getVagonsCount("Yarim ochiq vagon(????)", "VCHD-3","MDH(??????)"));
//			model.addAttribute("andjPoluvagon", vagonService.getVagonsCount("Yarim ochiq vagon(????)", "VCHD-5","MDH(??????)"));
//
//			//Tsisternalarni  hammasini olish
//			model.addAttribute("samTsisterna", vagonService.getVagonsCount("Sisterna(????)", "VCHD-6","MDH(??????)"));
//			model.addAttribute("havTsisterna", vagonService.getVagonsCount("Sisterna(????)", "VCHD-3","MDH(??????)"));
//			model.addAttribute("andjTsisterna", vagonService.getVagonsCount("Sisterna(????)", "VCHD-5","MDH(??????)"));
//
//			//Boshqalarni  hammasini olish
//			model.addAttribute("samBoshqa", vagonService.getVagonsCount("Boshqa turdagi(????????)", "VCHD-6","MDH(??????)"));
//			model.addAttribute("havBoshqa", vagonService.getVagonsCount("Boshqa turdagi(????????)", "VCHD-3","MDH(??????)"));
//			model.addAttribute("andjBoshqa", vagonService.getVagonsCount("Boshqa turdagi(????????)", "VCHD-5","MDH(??????)"));
//
//			// Jaminini olish
//			model.addAttribute("hammasi", vagonService.getCount("VCHD-6","MDH(??????)") +
//					vagonService.getCount("VCHD-3","MDH(??????)") +
//					vagonService.getCount("VCHD-5","MDH(??????)"));
//			model.addAttribute("sam", vagonService.getCount("VCHD-6","MDH(??????)"));
//			model.addAttribute("hav", vagonService.getCount("VCHD-3","MDH(??????)"));
//			model.addAttribute("andj", vagonService.getCount("VCHD-5","MDH(??????)"));
		} else if (country.equalsIgnoreCase("Sanoat(????????)")) {

			List<Integer> vagonsToDownloadTable  = new ArrayList<>();

			//Kritiylarni  hammasini olish
			int havKritiy = vagonService.getVagonsCount("Yopiq vagon (????????)", "VCHD-3","Sanoat(????????)");
			int andjKritiy = vagonService.getVagonsCount("Yopiq vagon (????????)", "VCHD-5","Sanoat(????????)");
			int samKritiy = vagonService.getVagonsCount("Yopiq vagon (????????)", "VCHD-6","Sanoat(????????)");

			model.addAttribute("samKritiy", samKritiy);
			model.addAttribute("havKritiy", havKritiy);
			model.addAttribute("andjKritiy", andjKritiy);

			vagonsToDownloadTable.add(havKritiy);
			vagonsToDownloadTable.add(andjKritiy);
			vagonsToDownloadTable.add(samKritiy);
			vagonsToDownloadTable.add(havKritiy + andjKritiy + samKritiy);

			//Platformalarni  hammasini olish
			int samPlatforma =  vagonService.getVagonsCount("Platforma(????)", "VCHD-6","Sanoat(????????)");
			int havPlatforma =  vagonService.getVagonsCount("Platforma(????)", "VCHD-3","Sanoat(????????)");
			int andjPlatforma =  vagonService.getVagonsCount("Platforma(????)", "VCHD-5","Sanoat(????????)");

			model.addAttribute("samPlatforma", samPlatforma);
			model.addAttribute("havPlatforma", havPlatforma);
			model.addAttribute("andjPlatforma", andjPlatforma);

			vagonsToDownloadTable.add(havPlatforma);
			vagonsToDownloadTable.add(andjPlatforma);
			vagonsToDownloadTable.add(samPlatforma);
			vagonsToDownloadTable.add(samPlatforma + andjPlatforma + havPlatforma);

			//Poluvagonlarni  hammasini olish
			int samPoluvagon = vagonService.getVagonsCount("Yarim ochiq vagon(????)", "VCHD-6","Sanoat(????????)");
			int havPoluvagon = vagonService.getVagonsCount("Yarim ochiq vagon(????)", "VCHD-3","Sanoat(????????)");
			int andjPoluvagon = vagonService.getVagonsCount("Yarim ochiq vagon(????)", "VCHD-5","Sanoat(????????)");

			model.addAttribute("samPoluvagon", samPoluvagon);
			model.addAttribute("havPoluvagon", havPoluvagon);
			model.addAttribute("andjPoluvagon", andjPoluvagon);

			vagonsToDownloadTable.add(havPoluvagon);
			vagonsToDownloadTable.add(andjPoluvagon);
			vagonsToDownloadTable.add(samPoluvagon);
			vagonsToDownloadTable.add(samPoluvagon + havPoluvagon + andjPoluvagon);

			//Tsisternalarni  hammasini olish
			int samTsisterna = vagonService.getVagonsCount("Sisterna(????)", "VCHD-6","Sanoat(????????)");
			int havTsisterna = vagonService.getVagonsCount("Sisterna(????)", "VCHD-3","Sanoat(????????)");
			int andjTsisterna = vagonService.getVagonsCount("Sisterna(????)", "VCHD-5","Sanoat(????????)");

			model.addAttribute("samTsisterna", samTsisterna);
			model.addAttribute("havTsisterna", havTsisterna);
			model.addAttribute("andjTsisterna", andjTsisterna);

			vagonsToDownloadTable.add(havTsisterna);
			vagonsToDownloadTable.add(andjTsisterna);
			vagonsToDownloadTable.add(samTsisterna);
			vagonsToDownloadTable.add(samTsisterna + andjTsisterna + havTsisterna);

			//Boshqalarni  hammasini olish
			int samBoshqa = vagonService.getVagonsCount("Boshqa turdagi(????????)", "VCHD-6","Sanoat(????????)");
			int havBoshqa = vagonService.getVagonsCount("Boshqa turdagi(????????)", "VCHD-3","Sanoat(????????)");
			int andjBoshqa = vagonService.getVagonsCount("Boshqa turdagi(????????)", "VCHD-5","Sanoat(????????)");

			model.addAttribute("samBoshqa", samBoshqa);
			model.addAttribute("havBoshqa", havBoshqa);
			model.addAttribute("andjBoshqa", andjBoshqa);

			vagonsToDownloadTable.add(havBoshqa);
			vagonsToDownloadTable.add(andjBoshqa);
			vagonsToDownloadTable.add(samBoshqa);
			vagonsToDownloadTable.add(samBoshqa + andjBoshqa + havBoshqa);

			// Jaminini olish
			int hammasi = vagonService.getCount("VCHD-6","Sanoat(????????)") + vagonService.getCount("VCHD-3","Sanoat(????????)") + vagonService.getCount("VCHD-5","Sanoat(????????)");
			int sam = vagonService.getCount("VCHD-6","Sanoat(????????)");
			int hav = vagonService.getCount("VCHD-3","Sanoat(????????)");
			int andj = vagonService.getCount("VCHD-5","Sanoat(????????)");

			model.addAttribute("hammasi", hammasi);
			model.addAttribute("sam", sam);
			model.addAttribute("hav", hav);
			model.addAttribute("andj", andj);

			vagonsToDownloadTable.add(hav);
			vagonsToDownloadTable.add(andj);
			vagonsToDownloadTable.add(sam);
			vagonsToDownloadTable.add(sam + hav + andj);

			vagonsToDownloadTables = vagonsToDownloadTable;

//			//Kritiylarni  hammasini olish
//			model.addAttribute("samKritiy", vagonService.getVagonsCount("Yopiq vagon (????????)", "VCHD-6","Sanoat(????????)"));
//			model.addAttribute("havKritiy", vagonService.getVagonsCount("Yopiq vagon (????????)", "VCHD-3","Sanoat(????????)"));
//			model.addAttribute("andjKritiy", vagonService.getVagonsCount("Yopiq vagon (????????)", "VCHD-5","Sanoat(????????)"));
//
//			//Platformalarni  hammasini olish
//			model.addAttribute("samPlatforma", vagonService.getVagonsCount("Platforma(????)", "VCHD-6","Sanoat(????????)"));
//			model.addAttribute("havPlatforma", vagonService.getVagonsCount("Platforma(????)", "VCHD-3","Sanoat(????????)"));
//			model.addAttribute("andjPlatforma", vagonService.getVagonsCount("Platforma(????)", "VCHD-5","Sanoat(????????)"));
//
//			//Poluvagonlarni  hammasini olish
//			model.addAttribute("samPoluvagon", vagonService.getVagonsCount("Yarim ochiq vagon(????)", "VCHD-6","Sanoat(????????)"));
//			model.addAttribute("havPoluvagon", vagonService.getVagonsCount("Yarim ochiq vagon(????)", "VCHD-3","Sanoat(????????)"));
//			model.addAttribute("andjPoluvagon", vagonService.getVagonsCount("Yarim ochiq vagon(????)", "VCHD-5","Sanoat(????????)"));
//
//			//Tsisternalarni  hammasini olish
//			model.addAttribute("samTsisterna", vagonService.getVagonsCount("Sisterna(????)", "VCHD-6","Sanoat(????????)"));
//			model.addAttribute("havTsisterna", vagonService.getVagonsCount("Sisterna(????)", "VCHD-3","Sanoat(????????)"));
//			model.addAttribute("andjTsisterna", vagonService.getVagonsCount("Sisterna(????)", "VCHD-5","Sanoat(????????)"));
//
//			//Boshqalarni  hammasini olish
//			model.addAttribute("samBoshqa", vagonService.getVagonsCount("Boshqa turdagi(????????)", "VCHD-6","Sanoat(????????)"));
//			model.addAttribute("havBoshqa", vagonService.getVagonsCount("Boshqa turdagi(????????)", "VCHD-3","Sanoat(????????)"));
//			model.addAttribute("andjBoshqa", vagonService.getVagonsCount("Boshqa turdagi(????????)", "VCHD-5","Sanoat(????????)"));
//
//			// Jaminini olish
//			model.addAttribute("hammasi", vagonService.getCount("VCHD-6","Sanoat(????????)") +
//					vagonService.getCount("VCHD-3","Sanoat(????????)") +
//					vagonService.getCount("VCHD-5","Sanoat(????????)"));
//			model.addAttribute("sam", vagonService.getCount("VCHD-6","Sanoat(????????)"));
//			model.addAttribute("hav", vagonService.getCount("VCHD-3","Sanoat(????????)"));
//			model.addAttribute("andj", vagonService.getCount("VCHD-5","Sanoat(????????)"));
		}
		return "vagons";
    }  

}
