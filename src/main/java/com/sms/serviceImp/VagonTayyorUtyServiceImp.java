package com.sms.serviceImp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.layout.border.Border;
import com.itextpdf.layout.property.TextAlignment;
import com.sms.dto.PlanUtyDto;
import com.sms.model.PlanUty;
import com.sms.model.LastActionTimes;
import com.sms.repository.PlanUtyRepository;
import com.sms.repository.TimeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.sms.model.VagonTayyorUty;
import com.sms.repository.VagonTayyorUtyRepository;
import com.sms.service.VagonTayyorUtyService;
import org.springframework.web.bind.annotation.RequestParam;

@Service
public class VagonTayyorUtyServiceImp implements VagonTayyorUtyService{

	@Autowired
	private VagonTayyorUtyRepository vagonTayyorUtyRepository;
	@Autowired
	private PlanUtyRepository planUtyRepository;
	@Autowired
	private TimeRepository utyTimeRepository;
	
	LocalDateTime today = LocalDateTime.now();
	LocalDateTime minusHours = today.plusHours(5);
	DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

	String currentDate = minusHours.format(myFormatObj);
	String samDate ;
	String havDate ;
	String andjDate ;
	
	public void createPdf(List<VagonTayyorUty> vagons, HttpServletResponse response) throws IOException {
		
		String home = System.getProperty("user.home");
		  File file = new File(home + "/Downloads" + "/O'TY rejasi boyicha ta'mir boyicha ma'lumot.pdf");
		  if (!file.getParentFile().exists())
		      file.getParentFile().mkdirs();
		  if (!file.exists())
		      file.createNewFile();
		List<VagonTayyorUty> allVagons = vagons;
		try {
			response.setHeader("Content-Disposition",
                    "attachment;fileName=\"" + "O'TY rejasi boyicha tamir boyicha malumot.pdf" +"\"");
			response.setContentType("application/pdf");
			
			PdfWriter writer = new PdfWriter(file.getAbsolutePath());
			PdfDocument pdfDoc = new PdfDocument(writer);
			Document doc = new Document(pdfDoc);

			String FONT_FILENAME = "./src/main/resources/arial.ttf";
			PdfFont font = PdfFontFactory.createFont(FONT_FILENAME, PdfEncodings.IDENTITY_H);
			doc.setFont(font);

			Paragraph paragraph = new Paragraph("Ta'mirdan chiqgan vagonlar(O'TY rejasi bo'yicha)");
			paragraph.setBackgroundColor(Color.DARK_GRAY);
			paragraph.setFontColor(Color.WHITE);// Setting background color to cell1
			paragraph.setBorder(Border.NO_BORDER);            // Setting border to cell1
			paragraph.setTextAlignment(TextAlignment.CENTER); // Setting text alignment to cell1
			paragraph.setFontSize(16);

			float[] columnWidth = {30f,200f,200f,200f,200f,200f,200f,200f,200f,200f};
			Table table = new Table(columnWidth);
			table.setTextAlignment(TextAlignment.CENTER);
			table.addCell(new Cell().add(" № "));
			table.addCell(new Cell().add("Nomeri"));
			table.addCell(new Cell().add("Vagon turi"));
			table.addCell(new Cell().add("VCHD"));
			table.addCell(new Cell().add("Ta'mir turi"));
			table.addCell(new Cell().add("Ishlab chiqarilgan yili"));
			table.addCell(new Cell().add("Ta'mirdan chiqgan vaqti"));
			table.addCell(new Cell().add("Saqlangan vaqti"));
			table.addCell(new Cell().add("Egasi"));
			table.addCell(new Cell().add("Izoh"));
			int i=0;
			for(VagonTayyorUty vagon:allVagons) {
				i++;
				table.addCell(new Cell().add(String.valueOf(i)));
				table.addCell(new Cell().add(String.valueOf(vagon.getNomer())));
				table.addCell(new Cell().add(vagon.getVagonTuri()));
				table.addCell(new Cell().add(vagon.getDepoNomi()));
				table.addCell(new Cell().add(vagon.getRemontTuri()));
				table.addCell(new Cell().add(String.valueOf(vagon.getIshlabChiqarilganYili())));
				table.addCell(new Cell().add(String.valueOf(vagon.getChiqganVaqti())));
				table.addCell(new Cell().add(String.valueOf(vagon.getCreatedDate())));
				table.addCell(new Cell().add(vagon.getCountry()));
				table.addCell(new Cell().add(vagon.getIzoh()));
			}

			doc.add(paragraph);
			doc.add(table);
			doc.close();
			FileInputStream in = new FileInputStream(file.getAbsoluteFile());
			FileCopyUtils.copy(in, response.getOutputStream());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}



	public String getSamDate() {
		Optional<LastActionTimes> optionalUtyTime = utyTimeRepository.findById(1);
		if (!optionalUtyTime.isPresent())
			return currentDate;
		return optionalUtyTime.get().getSamUtyDate();
	}

	public String getHavDate() {
		Optional<LastActionTimes> optionalUtyTime = utyTimeRepository.findById(1);
		if (!optionalUtyTime.isPresent())
			return currentDate;
		return optionalUtyTime.get().getHavUtyDate();
	}

	public String getAndjDate() {
		Optional<LastActionTimes> optionalUtyTime = utyTimeRepository.findById(1);
		if (!optionalUtyTime.isPresent())
			return currentDate;
		return optionalUtyTime.get().getAndjUtyDate();
	}

	// bosh admin qoshadi
	@Override
	public VagonTayyorUty saveVagon(VagonTayyorUty vagon) {	
		if(vagon.getNomer() == null)
			return new VagonTayyorUty();
		Optional<VagonTayyorUty> exist=	vagonTayyorUtyRepository.findByNomer(vagon.getNomer());
		if(exist.isPresent())
			return new VagonTayyorUty();
		VagonTayyorUty savedVagon = new VagonTayyorUty();
		savedVagon.setNomer(vagon.getNomer());
		savedVagon.setDepoNomi(vagon.getDepoNomi());
		savedVagon.setRemontTuri(vagon.getRemontTuri());
		savedVagon.setVagonTuri(vagon.getVagonTuri());
		savedVagon.setIshlabChiqarilganYili(vagon.getIshlabChiqarilganYili());
		savedVagon.setIzoh(vagon.getIzoh());
		savedVagon.setCountry(vagon.getCountry());
		savedVagon.setChiqganVaqti(vagon.getChiqganVaqti());
		savedVagon.setCountry("O'TY(ГАЖК)");
		savedVagon.setActive(true);

		String currentDate = minusHours.format(myFormatObj);
		savedVagon.setCreatedDate(currentDate);

		return vagonTayyorUtyRepository.save(savedVagon);	
	}

	@Override
	public VagonTayyorUty saveVagonSam(VagonTayyorUty vagon) {
		if(vagon.getNomer() == null)
			return new VagonTayyorUty();
		Optional<VagonTayyorUty> exist=	vagonTayyorUtyRepository.findByNomer(vagon.getNomer());
		if(exist.isPresent() || !vagon.getDepoNomi().equalsIgnoreCase("VCHD-6"))
			return new VagonTayyorUty();
		VagonTayyorUty savedVagon = new VagonTayyorUty();
		savedVagon.setNomer(vagon.getNomer());
		savedVagon.setDepoNomi(vagon.getDepoNomi());
		savedVagon.setRemontTuri(vagon.getRemontTuri());
		savedVagon.setVagonTuri(vagon.getVagonTuri());
		savedVagon.setIshlabChiqarilganYili(vagon.getIshlabChiqarilganYili());
		savedVagon.setIzoh(vagon.getIzoh());
		savedVagon.setCountry(vagon.getCountry());
		savedVagon.setChiqganVaqti(vagon.getChiqganVaqti());
		savedVagon.setCountry("O'TY(ГАЖК)");
		savedVagon.setActive(true);

		LocalDateTime today = LocalDateTime.now();
		LocalDateTime minusHours = today.plusHours(5);
		DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
		samDate = minusHours.format(myFormatObj);

		savedVagon.setCreatedDate(samDate);

		Optional<LastActionTimes> optionalUtyTime = utyTimeRepository.findById(1);
		optionalUtyTime.get().setSamUtyDate(samDate);
		utyTimeRepository.save(optionalUtyTime.get());

		return vagonTayyorUtyRepository.save(savedVagon);
	}
	
	@Override
	public VagonTayyorUty saveVagonHav(VagonTayyorUty vagon) {
		if(vagon.getNomer() == null)
			return new VagonTayyorUty();
		Optional<VagonTayyorUty> exist=	vagonTayyorUtyRepository.findByNomer(vagon.getNomer());
		if(exist.isPresent() || !vagon.getDepoNomi().equalsIgnoreCase("VCHD-3"))
			return new VagonTayyorUty();
		VagonTayyorUty savedVagon = new VagonTayyorUty();
		savedVagon.setNomer(vagon.getNomer());
		savedVagon.setDepoNomi(vagon.getDepoNomi());
		savedVagon.setRemontTuri(vagon.getRemontTuri());
		savedVagon.setVagonTuri(vagon.getVagonTuri());
		savedVagon.setIshlabChiqarilganYili(vagon.getIshlabChiqarilganYili());
		savedVagon.setIzoh(vagon.getIzoh());
		savedVagon.setCountry(vagon.getCountry());
		savedVagon.setChiqganVaqti(vagon.getChiqganVaqti());
		savedVagon.setCountry("O'TY(ГАЖК)");
		savedVagon.setActive(true);

		LocalDateTime today = LocalDateTime.now();
		LocalDateTime minusHours = today.plusHours(5);
		DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
		havDate = minusHours.format(myFormatObj);

		Optional<LastActionTimes> optionalUtyTime = utyTimeRepository.findById(1);
		savedVagon.setCreatedDate(havDate);
		optionalUtyTime.get().setHavUtyDate(havDate);
		utyTimeRepository.save(optionalUtyTime.get());

		return vagonTayyorUtyRepository.save(savedVagon);
	}

	@Override
	public VagonTayyorUty saveVagonAndj(VagonTayyorUty vagon) {
		if(vagon.getNomer() == null)
			return new VagonTayyorUty();
		Optional<VagonTayyorUty> exist= vagonTayyorUtyRepository.findByNomer(vagon.getNomer());
		if(exist.isPresent() || !vagon.getDepoNomi().equalsIgnoreCase("VCHD-5"))
			return new VagonTayyorUty();
		VagonTayyorUty savedVagon = new VagonTayyorUty();
		savedVagon.setNomer(vagon.getNomer());
		savedVagon.setDepoNomi(vagon.getDepoNomi());
		savedVagon.setRemontTuri(vagon.getRemontTuri());
		savedVagon.setVagonTuri(vagon.getVagonTuri());
		savedVagon.setIshlabChiqarilganYili(vagon.getIshlabChiqarilganYili());
		savedVagon.setIzoh(vagon.getIzoh());
		savedVagon.setCountry(vagon.getCountry());
		savedVagon.setChiqganVaqti(vagon.getChiqganVaqti());
		savedVagon.setCountry("O'TY(ГАЖК)");
		savedVagon.setActive(true);

		LocalDateTime today = LocalDateTime.now();
		LocalDateTime minusHours = today.plusHours(5);
		DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
		andjDate = minusHours.format(myFormatObj);

		Optional<LastActionTimes> optionalUtyTime = utyTimeRepository.findById(1);
		savedVagon.setCreatedDate(andjDate);
		optionalUtyTime.get().setAndjUtyDate(andjDate);
		utyTimeRepository.save(optionalUtyTime.get());

		return vagonTayyorUtyRepository.save(savedVagon);
}

	@Override
	public VagonTayyorUty updateVagon(VagonTayyorUty vagon, long id) {	
		if(vagon.getNomer() == null)
			return new VagonTayyorUty();
		 Optional<VagonTayyorUty> exist = vagonTayyorUtyRepository.findById(id);
		 if(!exist.isPresent() )
			 return new VagonTayyorUty();
		 VagonTayyorUty savedVagon = exist.get();
		 savedVagon.setId(id);
		 savedVagon.setNomer(vagon.getNomer());
		 savedVagon.setVagonTuri(vagon.getVagonTuri());
		 savedVagon.setDepoNomi(vagon.getDepoNomi());
		 savedVagon.setRemontTuri(vagon.getRemontTuri());
		 savedVagon.setIshlabChiqarilganYili(vagon.getIshlabChiqarilganYili());
		 savedVagon.setIzoh(vagon.getIzoh());
		 savedVagon.setCountry(vagon.getCountry());
		 savedVagon.setChiqganVaqti(vagon.getChiqganVaqti());
		 savedVagon.setCountry("O'TY(ГАЖК)");

		 return vagonTayyorUtyRepository.save(savedVagon);
	}
	
	@Override
	public VagonTayyorUty updateVagonSam(VagonTayyorUty vagon, long id) {
		if(vagon.getNomer() == null)
			return new VagonTayyorUty();
		 Optional<VagonTayyorUty> exist = vagonTayyorUtyRepository.findById(id);
		 if(exist.get().getDepoNomi().equalsIgnoreCase("VCHD-6") && vagon.getDepoNomi().equalsIgnoreCase("VCHD-6")) {
			 VagonTayyorUty savedVagon = exist.get();
			 savedVagon.setId(id);
			 savedVagon.setNomer(vagon.getNomer());
			 savedVagon.setVagonTuri(vagon.getVagonTuri());
			 savedVagon.setDepoNomi(vagon.getDepoNomi());
			 savedVagon.setRemontTuri(vagon.getRemontTuri());
			 savedVagon.setIshlabChiqarilganYili(vagon.getIshlabChiqarilganYili());
			 savedVagon.setIzoh(vagon.getIzoh());
			 savedVagon.setCountry(vagon.getCountry());
			 savedVagon.setChiqganVaqti(vagon.getChiqganVaqti());
   			 savedVagon.setCountry("O'TY(ГАЖК)");

			 LocalDateTime today = LocalDateTime.now();
			 LocalDateTime minusHours = today.plusHours(5);
			 DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
			 samDate = minusHours.format(myFormatObj);

			 Optional<LastActionTimes> optionalUtyTime = utyTimeRepository.findById(1);
			 optionalUtyTime.get().setSamUtyDate(samDate);
			 utyTimeRepository.save(optionalUtyTime.get());

			 return vagonTayyorUtyRepository.save(savedVagon);
		 }else
			return new VagonTayyorUty();

	}

	@Override
	public VagonTayyorUty updateVagonHav(VagonTayyorUty vagon, long id) {
		if(vagon.getNomer() == null)
			return new VagonTayyorUty();
		 Optional<VagonTayyorUty> exist = vagonTayyorUtyRepository.findById(id);
		 if(exist.get().getDepoNomi().equalsIgnoreCase("VCHD-3") && vagon.getDepoNomi().equalsIgnoreCase("VCHD-3") ) {
			 
			 VagonTayyorUty savedVagon = exist.get();
			 savedVagon.setId(id);
			 savedVagon.setNomer(vagon.getNomer());
			 savedVagon.setVagonTuri(vagon.getVagonTuri());
			 savedVagon.setDepoNomi(vagon.getDepoNomi());
			 savedVagon.setRemontTuri(vagon.getRemontTuri());
			 savedVagon.setIshlabChiqarilganYili(vagon.getIshlabChiqarilganYili());
			 savedVagon.setIzoh(vagon.getIzoh());
			 savedVagon.setCountry(vagon.getCountry());
			 savedVagon.setChiqganVaqti(vagon.getChiqganVaqti());
			 savedVagon.setCountry("O'TY(ГАЖК)");

			 Optional<LastActionTimes> optionalUtyTime = utyTimeRepository.findById(1);

			 LocalDateTime today = LocalDateTime.now();
			 LocalDateTime minusHours = today.plusHours(5);
			 DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
			 havDate = minusHours.format(myFormatObj);
			 optionalUtyTime.get().setHavUtyDate(havDate);
			 utyTimeRepository.save(optionalUtyTime.get());

			 return vagonTayyorUtyRepository.save(savedVagon);
		 }else
			 return new VagonTayyorUty();
	}

	@Override
	public VagonTayyorUty updateVagonAndj(VagonTayyorUty vagon, long id) {
		if(vagon.getNomer() == null)
			return new VagonTayyorUty();
		 Optional<VagonTayyorUty> exist = vagonTayyorUtyRepository.findById(id);
		 if( exist.get().getDepoNomi().equalsIgnoreCase("VCHD-5") && vagon.getDepoNomi().equalsIgnoreCase("VCHD-5") ){	
			 VagonTayyorUty savedVagon = exist.get();
			 savedVagon.setId(id);
			 savedVagon.setNomer(vagon.getNomer());
			 savedVagon.setVagonTuri(vagon.getVagonTuri());
			 savedVagon.setDepoNomi(vagon.getDepoNomi());
			 savedVagon.setRemontTuri(vagon.getRemontTuri());
			 savedVagon.setIshlabChiqarilganYili(vagon.getIshlabChiqarilganYili());
			 savedVagon.setIzoh(vagon.getIzoh());
			 savedVagon.setCountry(vagon.getCountry());
			 savedVagon.setChiqganVaqti(vagon.getChiqganVaqti());
			 savedVagon.setCountry("O'TY(ГАЖК)");

			 Optional<LastActionTimes> optionalUtyTime = utyTimeRepository.findById(1);

			 LocalDateTime today = LocalDateTime.now();
			 LocalDateTime minusHours = today.plusHours(5);
			 DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
			 andjDate = minusHours.format(myFormatObj);
			 optionalUtyTime.get().setAndjUtyDate(andjDate);
			 utyTimeRepository.save(optionalUtyTime.get());

			 return vagonTayyorUtyRepository.save(savedVagon);
		}else {
				 return new VagonTayyorUty();
		}
	}

	//*update JAmi oylarniki
	@Override
	public VagonTayyorUty updateVagonMonths(VagonTayyorUty vagon, long id) {	
		if(vagon.getNomer() == null)
			return new VagonTayyorUty();
		 Optional<VagonTayyorUty> exist = vagonTayyorUtyRepository.findById(id);
		 if(!exist.isPresent() )
			 return new VagonTayyorUty();
		 VagonTayyorUty savedVagon = exist.get();
		 savedVagon.setId(id);
		 savedVagon.setNomer(vagon.getNomer());
		 savedVagon.setVagonTuri(vagon.getVagonTuri());
		 savedVagon.setDepoNomi(vagon.getDepoNomi());
		 savedVagon.setRemontTuri(vagon.getRemontTuri());
		 savedVagon.setIshlabChiqarilganYili(vagon.getIshlabChiqarilganYili());
		 savedVagon.setIzoh(vagon.getIzoh());
		 savedVagon.setCountry(vagon.getCountry());
		 savedVagon.setChiqganVaqti(vagon.getChiqganVaqti());
		 savedVagon.setCountry("O'TY(ГАЖК)");

		 return vagonTayyorUtyRepository.save(savedVagon);
	}
	
	@Override
	public VagonTayyorUty updateVagonSamMonths(VagonTayyorUty vagon, long id) {
		if(vagon.getNomer() == null)
			return new VagonTayyorUty();
		 Optional<VagonTayyorUty> exist = vagonTayyorUtyRepository.findById(id);
		 if(exist.get().getDepoNomi().equalsIgnoreCase("VCHD-6") && vagon.getDepoNomi().equalsIgnoreCase("VCHD-6")) {
			 VagonTayyorUty savedVagon = exist.get();
			 savedVagon.setId(id);
			 savedVagon.setNomer(vagon.getNomer());
			 savedVagon.setVagonTuri(vagon.getVagonTuri());
			 savedVagon.setDepoNomi(vagon.getDepoNomi());
			 savedVagon.setRemontTuri(vagon.getRemontTuri());
			 savedVagon.setIshlabChiqarilganYili(vagon.getIshlabChiqarilganYili());
			 savedVagon.setIzoh(vagon.getIzoh());
			 savedVagon.setCountry(vagon.getCountry());
			 savedVagon.setChiqganVaqti(vagon.getChiqganVaqti());
   			 savedVagon.setCountry("O'TY(ГАЖК)");

			 return vagonTayyorUtyRepository.save(savedVagon);
		 }else
			return new VagonTayyorUty();

	}

	@Override
	public VagonTayyorUty updateVagonHavMonths(VagonTayyorUty vagon, long id) {
		if(vagon.getNomer() == null)
			return new VagonTayyorUty();
		 Optional<VagonTayyorUty> exist = vagonTayyorUtyRepository.findById(id);
		 if(exist.get().getDepoNomi().equalsIgnoreCase("VCHD-3") && vagon.getDepoNomi().equalsIgnoreCase("VCHD-3") ) {
			 
			 VagonTayyorUty savedVagon = exist.get();
			 savedVagon.setId(id);
			 savedVagon.setNomer(vagon.getNomer());
			 savedVagon.setVagonTuri(vagon.getVagonTuri());
			 savedVagon.setDepoNomi(vagon.getDepoNomi());
			 savedVagon.setRemontTuri(vagon.getRemontTuri());
			 savedVagon.setIshlabChiqarilganYili(vagon.getIshlabChiqarilganYili());
			 savedVagon.setIzoh(vagon.getIzoh());
			 savedVagon.setCountry(vagon.getCountry());
			 savedVagon.setChiqganVaqti(vagon.getChiqganVaqti());
			 savedVagon.setCountry("O'TY(ГАЖК)");

			 return vagonTayyorUtyRepository.save(savedVagon);
		 }else
			 return new VagonTayyorUty();
	}

	@Override
	public VagonTayyorUty updateVagonAndjMonths(VagonTayyorUty vagon, long id) {
		if(vagon.getNomer() == null)
			return new VagonTayyorUty();
		 Optional<VagonTayyorUty> exist = vagonTayyorUtyRepository.findById(id);
		 if( exist.get().getDepoNomi().equalsIgnoreCase("VCHD-5") && vagon.getDepoNomi().equalsIgnoreCase("VCHD-5") ){	
			 VagonTayyorUty savedVagon = exist.get();
			 savedVagon.setId(id);
			 savedVagon.setNomer(vagon.getNomer());
			 savedVagon.setVagonTuri(vagon.getVagonTuri());
			 savedVagon.setDepoNomi(vagon.getDepoNomi());
			 savedVagon.setRemontTuri(vagon.getRemontTuri());
			 savedVagon.setIshlabChiqarilganYili(vagon.getIshlabChiqarilganYili());
			 savedVagon.setIzoh(vagon.getIzoh());
			 savedVagon.setCountry(vagon.getCountry());
			 savedVagon.setChiqganVaqti(vagon.getChiqganVaqti());
			 savedVagon.setCountry("O'TY(ГАЖК)");

			 return vagonTayyorUtyRepository.save(savedVagon);
		}else {
				 return new VagonTayyorUty();
		}
	}

	@Override
	public VagonTayyorUty getVagonById(long id) {
	Optional<VagonTayyorUty> exist=	vagonTayyorUtyRepository.findById(id);
	if(!exist.isPresent())
		return new VagonTayyorUty();
	return exist.get();
	}

	@Override
	public void deleteVagonById(long id) throws NotFoundException {
		Optional<VagonTayyorUty> exist=	vagonTayyorUtyRepository.findById(id);
		if(exist.isPresent()) 
			vagonTayyorUtyRepository.deleteById(id);

	}

	@Override
	public void deleteVagonSam(long id) throws NotFoundException {
		VagonTayyorUty exist=	vagonTayyorUtyRepository.findById(id).get();	
		if(exist.getDepoNomi().equals("VCHD-6") ) {
			vagonTayyorUtyRepository.deleteById(id);

			LocalDateTime today = LocalDateTime.now();
			LocalDateTime minusHours = today.plusHours(5);
			DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
			samDate = minusHours.format(myFormatObj);

			Optional<LastActionTimes> optionalUtyTime = utyTimeRepository.findById(1);
			optionalUtyTime.get().setSamUtyDate(samDate);
			utyTimeRepository.save(optionalUtyTime.get());
		}
	}

	@Override
	public void deleteVagonHav(long id) throws NotFoundException{
		VagonTayyorUty exist=	vagonTayyorUtyRepository.findById(id).get();	
		if(exist.getDepoNomi().equals("VCHD-3") ) {
			vagonTayyorUtyRepository.deleteById(id);


			LocalDateTime today = LocalDateTime.now();
			LocalDateTime minusHours = today.plusHours(5);
			DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
			Optional<LastActionTimes> optionalUtyTime = utyTimeRepository.findById(1);
			optionalUtyTime.get().setHavUtyDate(havDate);
			utyTimeRepository.save(optionalUtyTime.get());
		}
	}

	@Override
	public void deleteVagonAndj(long id) throws NotFoundException{
		VagonTayyorUty exist=	vagonTayyorUtyRepository.findById(id).get();	
		if(exist.getDepoNomi().equals("VCHD-5") ) {
			vagonTayyorUtyRepository.deleteById(id);


			LocalDateTime today = LocalDateTime.now();
			LocalDateTime minusHours = today.plusHours(5);
			DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
			andjDate = minusHours.format(myFormatObj);
			Optional<LastActionTimes> optionalUtyTime = utyTimeRepository.findById(1);
			optionalUtyTime.get().setAndjUtyDate(andjDate);
			utyTimeRepository.save(optionalUtyTime.get());
		}
	}

	@Override
	public int countByDepoNomiVagonTuriAndTamirTuri(String depoNomi, String vagonTuri, String tamirTuri) {
		return vagonTayyorUtyRepository.countByDepoNomiVagonTuriAndTamirTuri(depoNomi, vagonTuri, tamirTuri);
	}

	@Override
	public List<VagonTayyorUty> findAll() {
		return vagonTayyorUtyRepository.findAll();
	}
	@Override
	public List<VagonTayyorUty> findAll(String oy) {
		Optional<LastActionTimes> byId = utyTimeRepository.findById(1);
		if (!byId.isPresent()) {
			LastActionTimes times = new LastActionTimes();

			times.setId(1);
			times.setSamQoldiqDate(currentDate);
			times.setHavQoldiqDate(currentDate);
			times.setAndjQoldiqDate(currentDate);

			times.setSamMalumotDate(currentDate);
			times.setHavMalumotDate(currentDate);
			times.setAndjMalumotDate(currentDate);

			times.setSamUtyDate(currentDate);
			times.setHavUtyDate(currentDate);
			times.setAndjUtyDate(currentDate);

			times.setSamBiznesDate(currentDate);
			times.setHavBiznesDate(currentDate);
			times.setAndjBiznesDate(currentDate);

			utyTimeRepository.save(times);
		}
		return vagonTayyorUtyRepository.findAll(oy);
	}
	
	@Override
	public int countAllActiveByDepoNomiVagonTuriAndTamirTuri(String depoNomi, String vagnTuri, 
			String tamirTuri, String oy) {
		return vagonTayyorUtyRepository.countAllActiveByDepoNomiVagonTuriAndTamirTuri(depoNomi, vagnTuri, tamirTuri, oy);
	}
	
	@Override
	public VagonTayyorUty searchByNomer(Integer nomer, String oy) {
		return vagonTayyorUtyRepository.searchByNomer(nomer, oy);
	}

	@Override
	public VagonTayyorUty findByNomer(Integer nomer) {
		Optional<VagonTayyorUty> optional = vagonTayyorUtyRepository.findByNomer(nomer);
		if (optional.isPresent())
			return optional.get();
		return new VagonTayyorUty();
	}
	
	// filterniki	
	@Override
	public List<VagonTayyorUty> findAllByDepoNomiAndVagonTuri(String depoNomi, String vagonTuri, String oy) {
		return vagonTayyorUtyRepository.findAllByDepoNomiAndVagonTuri(depoNomi, vagonTuri, oy);
	}

	@Override
	public List<VagonTayyorUty> findAllByDepoNomi(String depoNomi, String oy) {
		return vagonTayyorUtyRepository.findAllByDepoNomi(depoNomi, oy);
	}

	@Override
	public List<VagonTayyorUty> findAllByVagonTuri(String vagonTuri, String oy) {
		return vagonTayyorUtyRepository.findAllByVagonTuri(vagonTuri, oy);
	}

	//hamma oylarini filter uchn

	@Override
	public List<VagonTayyorUty> findByDepoNomiAndVagonTuri(String depoNomi, String vagonTuri) {
		return vagonTayyorUtyRepository.findByDepoNomiAndVagonTuri(depoNomi, vagonTuri);
	}

	@Override
	public List<VagonTayyorUty> findByDepoNomi(String depoNomi) {
		return vagonTayyorUtyRepository.findByDepoNomi(depoNomi);
	}

	@Override
	public List<VagonTayyorUty> findByVagonTuri(String vagonTuri) {
		return vagonTayyorUtyRepository.findByVagonTuri(vagonTuri);
	}
	

	@Override
	public void savePlan(PlanUtyDto planDto) {
		
		Optional<PlanUty> existsPlan = planUtyRepository.findById(1);

		if (!existsPlan.isPresent()) {

			PlanUty utyPlan = new PlanUty();
			utyPlan.setId(1);

			//bir oy uchun
			//Depoli tamir 
			utyPlan.setSamDtKritiPlanUty(planDto.getSamDtKritiPlanUty());
			utyPlan.setSamDtPlatformaPlanUty(planDto.getSamDtPlatformaPlanUty());
			utyPlan.setSamDtPoluvagonPlanUty(planDto.getSamDtPoluvagonPlanUty());
			utyPlan.setSamDtSisternaPlanUty(planDto.getSamDtSisternaPlanUty());
			utyPlan.setSamDtBoshqaPlanUty(planDto.getSamDtBoshqaPlanUty());
			
			utyPlan.setHavDtKritiPlanUty(planDto.getHavDtKritiPlanUty());
			utyPlan.setHavDtPlatformaPlanUty(planDto.getHavDtPlatformaPlanUty());
			utyPlan.setHavDtPoluvagonPlanUty(planDto.getHavDtPoluvagonPlanUty());
			utyPlan.setHavDtSisternaPlanUty(planDto.getHavDtSisternaPlanUty());
			utyPlan.setHavDtBoshqaPlanUty(planDto.getHavDtBoshqaPlanUty());
			
			utyPlan.setAndjDtKritiPlanUty(planDto.getAndjDtKritiPlanUty());
			utyPlan.setAndjDtPlatformaPlanUty(planDto.getAndjDtPlatformaPlanUty());
			utyPlan.setAndjDtPoluvagonPlanUty(planDto.getAndjDtPoluvagonPlanUty());
			utyPlan.setAndjDtSisternaPlanUty(planDto.getAndjDtSisternaPlanUty());
			utyPlan.setAndjDtBoshqaPlanUty(planDto.getAndjDtBoshqaPlanUty());
			
			//kapital tamir 
			utyPlan.setSamKtKritiPlanUty(planDto.getSamKtKritiPlanUty());
			utyPlan.setSamKtPlatformaPlanUty(planDto.getSamKtPlatformaPlanUty());
			utyPlan.setSamKtPoluvagonPlanUty(planDto.getSamKtPoluvagonPlanUty());
			utyPlan.setSamKtSisternaPlanUty(planDto.getSamKtSisternaPlanUty());
			utyPlan.setSamKtBoshqaPlanUty(planDto.getSamKtBoshqaPlanUty());
			
			utyPlan.setHavKtKritiPlanUty(planDto.getHavKtKritiPlanUty());
			utyPlan.setHavKtPlatformaPlanUty(planDto.getHavKtPlatformaPlanUty());
			utyPlan.setHavKtPoluvagonPlanUty(planDto.getHavKtPoluvagonPlanUty());
			utyPlan.setHavKtSisternaPlanUty(planDto.getHavKtSisternaPlanUty());
			utyPlan.setHavKtBoshqaPlanUty(planDto.getHavKtBoshqaPlanUty());
			
			utyPlan.setAndjKtKritiPlanUty(planDto.getAndjKtKritiPlanUty());
			utyPlan.setAndjKtPlatformaPlanUty(planDto.getAndjKtPlatformaPlanUty());
			utyPlan.setAndjKtPoluvagonPlanUty(planDto.getAndjKtPoluvagonPlanUty());
			utyPlan.setAndjKtSisternaPlanUty(planDto.getAndjKtSisternaPlanUty());
			utyPlan.setAndjKtBoshqaPlanUty(planDto.getAndjKtBoshqaPlanUty());
			
			//KRP tamir 
			utyPlan.setSamKrpKritiPlanUty(planDto.getSamKrpKritiPlanUty());
			utyPlan.setSamKrpPlatformaPlanUty(planDto.getSamKrpPlatformaPlanUty());
			utyPlan.setSamKrpPoluvagonPlanUty(planDto.getSamKrpPoluvagonPlanUty());
			utyPlan.setSamKrpSisternaPlanUty(planDto.getSamKrpSisternaPlanUty());
			utyPlan.setSamKrpBoshqaPlanUty(planDto.getSamKrpBoshqaPlanUty());
			
			utyPlan.setHavKrpKritiPlanUty(planDto.getHavKrpKritiPlanUty());
			utyPlan.setHavKrpPlatformaPlanUty(planDto.getHavKrpPlatformaPlanUty());
			utyPlan.setHavKrpPoluvagonPlanUty(planDto.getHavKrpPoluvagonPlanUty());
			utyPlan.setHavKrpSisternaPlanUty(planDto.getHavKrpSisternaPlanUty());
			utyPlan.setHavKrpBoshqaPlanUty(planDto.getHavKrpBoshqaPlanUty());
			
			utyPlan.setAndjKrpKritiPlanUty(planDto.getAndjKrpKritiPlanUty());
			utyPlan.setAndjKrpPlatformaPlanUty(planDto.getAndjKrpPlatformaPlanUty());
			utyPlan.setAndjKrpPoluvagonPlanUty(planDto.getAndjKrpPoluvagonPlanUty());
			utyPlan.setAndjKrpSisternaPlanUty(planDto.getAndjKrpSisternaPlanUty());
			utyPlan.setAndjKrpBoshqaPlanUty(planDto.getAndjKrpBoshqaPlanUty());

			//Jami oy uchun
			//Depoli tamir
			utyPlan.setSamDtKritiPlanUtyMonths(planDto.getSamDtKritiPlanUty());
			utyPlan.setSamDtPlatformaPlanUtyMonths(planDto.getSamDtPlatformaPlanUty());
			utyPlan.setSamDtPoluvagonPlanUtyMonths(planDto.getSamDtPoluvagonPlanUty());
			utyPlan.setSamDtSisternaPlanUtyMonths(planDto.getSamDtSisternaPlanUty());
			utyPlan.setSamDtBoshqaPlanUtyMonths(planDto.getSamDtBoshqaPlanUty());

			utyPlan.setHavDtKritiPlanUtyMonths(planDto.getHavDtKritiPlanUty());
			utyPlan.setHavDtPlatformaPlanUtyMonths(planDto.getHavDtPlatformaPlanUty());
			utyPlan.setHavDtPoluvagonPlanUtyMonths(planDto.getHavDtPoluvagonPlanUty());
			utyPlan.setHavDtSisternaPlanUtyMonths(planDto.getHavDtSisternaPlanUty());
			utyPlan.setHavDtBoshqaPlanUtyMonths(planDto.getHavDtBoshqaPlanUty());

			utyPlan.setAndjDtKritiPlanUtyMonths(planDto.getAndjDtKritiPlanUty());
			utyPlan.setAndjDtPlatformaPlanUtyMonths(planDto.getAndjDtPlatformaPlanUty());
			utyPlan.setAndjDtPoluvagonPlanUtyMonths(planDto.getAndjDtPoluvagonPlanUty());
			utyPlan.setAndjDtSisternaPlanUtyMonths(planDto.getAndjDtSisternaPlanUty());
			utyPlan.setAndjDtBoshqaPlanUtyMonths(planDto.getAndjDtBoshqaPlanUty());

			//kapital tamir
			utyPlan.setSamKtKritiPlanUtyMonths(planDto.getSamKtKritiPlanUty());
			utyPlan.setSamKtPlatformaPlanUtyMonths(planDto.getSamKtPlatformaPlanUty());
			utyPlan.setSamKtPoluvagonPlanUtyMonths(planDto.getSamKtPoluvagonPlanUty());
			utyPlan.setSamKtSisternaPlanUtyMonths(planDto.getSamKtSisternaPlanUty());
			utyPlan.setSamKtBoshqaPlanUtyMonths(planDto.getSamKtBoshqaPlanUty());

			utyPlan.setHavKtKritiPlanUtyMonths(planDto.getHavKtKritiPlanUty());
			utyPlan.setHavKtPlatformaPlanUtyMonths(planDto.getHavKtPlatformaPlanUty());
			utyPlan.setHavKtPoluvagonPlanUtyMonths(planDto.getHavKtPoluvagonPlanUty());
			utyPlan.setHavKtSisternaPlanUtyMonths(planDto.getHavKtSisternaPlanUty());
			utyPlan.setHavKtBoshqaPlanUtyMonths(planDto.getHavKtBoshqaPlanUty());

			utyPlan.setAndjKtKritiPlanUtyMonths(planDto.getAndjKtKritiPlanUty());
			utyPlan.setAndjKtPlatformaPlanUtyMonths(planDto.getAndjKtPlatformaPlanUty());
			utyPlan.setAndjKtPoluvagonPlanUtyMonths(planDto.getAndjKtPoluvagonPlanUty());
			utyPlan.setAndjKtSisternaPlanUtyMonths(planDto.getAndjKtSisternaPlanUty());
			utyPlan.setAndjKtBoshqaPlanUtyMonths(planDto.getAndjKtBoshqaPlanUty());

			//KRP tamir
			utyPlan.setSamKrpKritiPlanUtyMonths(planDto.getSamKrpKritiPlanUty());
			utyPlan.setSamKrpPlatformaPlanUtyMonths(planDto.getSamKrpPlatformaPlanUty());
			utyPlan.setSamKrpPoluvagonPlanUtyMonths(planDto.getSamKrpPoluvagonPlanUty());
			utyPlan.setSamKrpSisternaPlanUtyMonths(planDto.getSamKrpSisternaPlanUty());
			utyPlan.setSamKrpBoshqaPlanUtyMonths(planDto.getSamKrpBoshqaPlanUty());

			utyPlan.setHavKrpKritiPlanUtyMonths(planDto.getHavKrpKritiPlanUty());
			utyPlan.setHavKrpPlatformaPlanUtyMonths(planDto.getHavKrpPlatformaPlanUty());
			utyPlan.setHavKrpPoluvagonPlanUtyMonths(planDto.getHavKrpPoluvagonPlanUty());
			utyPlan.setHavKrpSisternaPlanUtyMonths(planDto.getHavKrpSisternaPlanUty());
			utyPlan.setHavKrpBoshqaPlanUtyMonths(planDto.getHavKrpBoshqaPlanUty());

			utyPlan.setAndjKrpKritiPlanUtyMonths(planDto.getAndjKrpKritiPlanUty());
			utyPlan.setAndjKrpPlatformaPlanUtyMonths(planDto.getAndjKrpPlatformaPlanUty());
			utyPlan.setAndjKrpPoluvagonPlanUtyMonths(planDto.getAndjKrpPoluvagonPlanUty());
			utyPlan.setAndjKrpSisternaPlanUtyMonths(planDto.getAndjKrpSisternaPlanUty());
			utyPlan.setAndjKrpBoshqaPlanUtyMonths(planDto.getAndjKrpBoshqaPlanUty());

			planUtyRepository.save(utyPlan);

		}else {
			PlanUty utyPlan = existsPlan.get();

			//bir oy uchun
			//Depoli tamir
			utyPlan.setSamDtKritiPlanUty(planDto.getSamDtKritiPlanUty());
			utyPlan.setSamDtPlatformaPlanUty(planDto.getSamDtPlatformaPlanUty());
			utyPlan.setSamDtPoluvagonPlanUty(planDto.getSamDtPoluvagonPlanUty());
			utyPlan.setSamDtSisternaPlanUty(planDto.getSamDtSisternaPlanUty());
			utyPlan.setSamDtBoshqaPlanUty(planDto.getSamDtBoshqaPlanUty());

			utyPlan.setHavDtKritiPlanUty(planDto.getHavDtKritiPlanUty());
			utyPlan.setHavDtPlatformaPlanUty(planDto.getHavDtPlatformaPlanUty());
			utyPlan.setHavDtPoluvagonPlanUty(planDto.getHavDtPoluvagonPlanUty());
			utyPlan.setHavDtSisternaPlanUty(planDto.getHavDtSisternaPlanUty());
			utyPlan.setHavDtBoshqaPlanUty(planDto.getHavDtBoshqaPlanUty());

			utyPlan.setAndjDtKritiPlanUty(planDto.getAndjDtKritiPlanUty());
			utyPlan.setAndjDtPlatformaPlanUty(planDto.getAndjDtPlatformaPlanUty());
			utyPlan.setAndjDtPoluvagonPlanUty(planDto.getAndjDtPoluvagonPlanUty());
			utyPlan.setAndjDtSisternaPlanUty(planDto.getAndjDtSisternaPlanUty());
			utyPlan.setAndjDtBoshqaPlanUty(planDto.getAndjDtBoshqaPlanUty());

			//kapital tamir
			utyPlan.setSamKtKritiPlanUty(planDto.getSamKtKritiPlanUty());
			utyPlan.setSamKtPlatformaPlanUty(planDto.getSamKtPlatformaPlanUty());
			utyPlan.setSamKtPoluvagonPlanUty(planDto.getSamKtPoluvagonPlanUty());
			utyPlan.setSamKtSisternaPlanUty(planDto.getSamKtSisternaPlanUty());
			utyPlan.setSamKtBoshqaPlanUty(planDto.getSamKtBoshqaPlanUty());

			utyPlan.setHavKtKritiPlanUty(planDto.getHavKtKritiPlanUty());
			utyPlan.setHavKtPlatformaPlanUty(planDto.getHavKtPlatformaPlanUty());
			utyPlan.setHavKtPoluvagonPlanUty(planDto.getHavKtPoluvagonPlanUty());
			utyPlan.setHavKtSisternaPlanUty(planDto.getHavKtSisternaPlanUty());
			utyPlan.setHavKtBoshqaPlanUty(planDto.getHavKtBoshqaPlanUty());

			utyPlan.setAndjKtKritiPlanUty(planDto.getAndjKtKritiPlanUty());
			utyPlan.setAndjKtPlatformaPlanUty(planDto.getAndjKtPlatformaPlanUty());
			utyPlan.setAndjKtPoluvagonPlanUty(planDto.getAndjKtPoluvagonPlanUty());
			utyPlan.setAndjKtSisternaPlanUty(planDto.getAndjKtSisternaPlanUty());
			utyPlan.setAndjKtBoshqaPlanUty(planDto.getAndjKtBoshqaPlanUty());

			//KRP tamir
			utyPlan.setSamKrpKritiPlanUty(planDto.getSamKrpKritiPlanUty());
			utyPlan.setSamKrpPlatformaPlanUty(planDto.getSamKrpPlatformaPlanUty());
			utyPlan.setSamKrpPoluvagonPlanUty(planDto.getSamKrpPoluvagonPlanUty());
			utyPlan.setSamKrpSisternaPlanUty(planDto.getSamKrpSisternaPlanUty());
			utyPlan.setSamKrpBoshqaPlanUty(planDto.getSamKrpBoshqaPlanUty());

			utyPlan.setHavKrpKritiPlanUty(planDto.getHavKrpKritiPlanUty());
			utyPlan.setHavKrpPlatformaPlanUty(planDto.getHavKrpPlatformaPlanUty());
			utyPlan.setHavKrpPoluvagonPlanUty(planDto.getHavKrpPoluvagonPlanUty());
			utyPlan.setHavKrpSisternaPlanUty(planDto.getHavKrpSisternaPlanUty());
			utyPlan.setHavKrpBoshqaPlanUty(planDto.getHavKrpBoshqaPlanUty());

			utyPlan.setAndjKrpKritiPlanUty(planDto.getAndjKrpKritiPlanUty());
			utyPlan.setAndjKrpPlatformaPlanUty(planDto.getAndjKrpPlatformaPlanUty());
			utyPlan.setAndjKrpPoluvagonPlanUty(planDto.getAndjKrpPoluvagonPlanUty());
			utyPlan.setAndjKrpSisternaPlanUty(planDto.getAndjKrpSisternaPlanUty());
			utyPlan.setAndjKrpBoshqaPlanUty(planDto.getAndjKrpBoshqaPlanUty());

			//Jami oy uchun
			//Depoli tamir
			utyPlan.setSamDtKritiPlanUtyMonths(utyPlan.getSamDtKritiPlanUtyMonths() + planDto.getSamDtKritiPlanUty());
			utyPlan.setSamDtPlatformaPlanUtyMonths(utyPlan.getSamDtPlatformaPlanUtyMonths() +planDto.getSamDtPlatformaPlanUty());
			utyPlan.setSamDtPoluvagonPlanUtyMonths(utyPlan.getSamDtPoluvagonPlanUtyMonths() +planDto.getSamDtPoluvagonPlanUty());
			utyPlan.setSamDtSisternaPlanUtyMonths(utyPlan.getSamDtSisternaPlanUtyMonths() +planDto.getSamDtSisternaPlanUty());
			utyPlan.setSamDtBoshqaPlanUtyMonths(utyPlan.getSamDtBoshqaPlanUtyMonths() +planDto.getSamDtBoshqaPlanUty());

			utyPlan.setHavDtKritiPlanUtyMonths(utyPlan.getHavDtKritiPlanUtyMonths() + planDto.getHavDtKritiPlanUty());
			utyPlan.setHavDtPlatformaPlanUtyMonths(utyPlan.getHavDtPlatformaPlanUtyMonths() +planDto.getHavDtPlatformaPlanUty());
			utyPlan.setHavDtPoluvagonPlanUtyMonths(utyPlan.getHavDtPoluvagonPlanUtyMonths() +planDto.getHavDtPoluvagonPlanUty());
			utyPlan.setHavDtSisternaPlanUtyMonths(utyPlan.getHavDtSisternaPlanUtyMonths() +planDto.getHavDtSisternaPlanUty());
			utyPlan.setHavDtBoshqaPlanUtyMonths(utyPlan.getHavDtBoshqaPlanUtyMonths() +planDto.getHavDtBoshqaPlanUty());

			utyPlan.setAndjDtKritiPlanUtyMonths(utyPlan.getAndjDtKritiPlanUtyMonths() + planDto.getAndjDtKritiPlanUty());
			utyPlan.setAndjDtPlatformaPlanUtyMonths(utyPlan.getAndjDtPlatformaPlanUtyMonths() +planDto.getAndjDtPlatformaPlanUty());
			utyPlan.setAndjDtPoluvagonPlanUtyMonths(utyPlan.getAndjDtPoluvagonPlanUtyMonths() +planDto.getAndjDtPoluvagonPlanUty());
			utyPlan.setAndjDtSisternaPlanUtyMonths(utyPlan.getAndjDtSisternaPlanUtyMonths() +planDto.getAndjDtSisternaPlanUty());
			utyPlan.setAndjDtBoshqaPlanUtyMonths(utyPlan.getAndjDtBoshqaPlanUtyMonths() +planDto.getAndjDtBoshqaPlanUty());

			//kapital tamir
			utyPlan.setSamKtKritiPlanUtyMonths(utyPlan.getSamKtKritiPlanUtyMonths() + planDto.getSamKtKritiPlanUty());
			utyPlan.setSamKtPlatformaPlanUtyMonths(utyPlan.getSamKtPlatformaPlanUtyMonths() +planDto.getSamKtPlatformaPlanUty());
			utyPlan.setSamKtPoluvagonPlanUtyMonths(utyPlan.getSamKtPoluvagonPlanUtyMonths() +planDto.getSamKtPoluvagonPlanUty());
			utyPlan.setSamKtSisternaPlanUtyMonths(utyPlan.getSamKtSisternaPlanUtyMonths() +planDto.getSamKtSisternaPlanUty());
			utyPlan.setSamKtBoshqaPlanUtyMonths(utyPlan.getSamKtBoshqaPlanUtyMonths() +planDto.getSamKtBoshqaPlanUty());

			utyPlan.setHavKtKritiPlanUtyMonths(utyPlan.getHavKtKritiPlanUtyMonths() + planDto.getHavKtKritiPlanUty());
			utyPlan.setHavKtPlatformaPlanUtyMonths(utyPlan.getHavKtPlatformaPlanUtyMonths() +planDto.getHavKtPlatformaPlanUty());
			utyPlan.setHavKtPoluvagonPlanUtyMonths(utyPlan.getHavKtPoluvagonPlanUtyMonths() +planDto.getHavKtPoluvagonPlanUty());
			utyPlan.setHavKtSisternaPlanUtyMonths(utyPlan.getHavKtSisternaPlanUtyMonths() +planDto.getHavKtSisternaPlanUty());
			utyPlan.setHavKtBoshqaPlanUtyMonths(utyPlan.getHavKtBoshqaPlanUtyMonths() +planDto.getHavKtBoshqaPlanUty());

			utyPlan.setAndjKtKritiPlanUtyMonths(utyPlan.getAndjKtKritiPlanUtyMonths() + planDto.getAndjKtKritiPlanUty());
			utyPlan.setAndjKtPlatformaPlanUtyMonths(utyPlan.getAndjKtPlatformaPlanUtyMonths() +planDto.getAndjKtPlatformaPlanUty());
			utyPlan.setAndjKtPoluvagonPlanUtyMonths(utyPlan.getAndjKtPoluvagonPlanUtyMonths() +planDto.getAndjKtPoluvagonPlanUty());
			utyPlan.setAndjKtSisternaPlanUtyMonths(utyPlan.getAndjKtSisternaPlanUtyMonths() +planDto.getAndjKtSisternaPlanUty());
			utyPlan.setAndjKtBoshqaPlanUtyMonths(utyPlan.getAndjKtBoshqaPlanUtyMonths() +planDto.getAndjKtBoshqaPlanUty());

			//KRP tamir
			utyPlan.setSamKrpKritiPlanUtyMonths(utyPlan.getSamKrpKritiPlanUtyMonths() + planDto.getSamKrpKritiPlanUty());
			utyPlan.setSamKrpPlatformaPlanUtyMonths(utyPlan.getSamKrpPlatformaPlanUtyMonths() +planDto.getSamKrpPlatformaPlanUty());
			utyPlan.setSamKrpPoluvagonPlanUtyMonths(utyPlan.getSamKrpPoluvagonPlanUtyMonths() +planDto.getSamKrpPoluvagonPlanUty());
			utyPlan.setSamKrpSisternaPlanUtyMonths(utyPlan.getSamKrpSisternaPlanUtyMonths() +planDto.getSamKrpSisternaPlanUty());
			utyPlan.setSamKrpBoshqaPlanUtyMonths(utyPlan.getSamKrpBoshqaPlanUtyMonths() +planDto.getSamKrpBoshqaPlanUty());

			utyPlan.setHavKrpKritiPlanUtyMonths(utyPlan.getHavKrpKritiPlanUtyMonths() + planDto.getHavKrpKritiPlanUty());
			utyPlan.setHavKrpPlatformaPlanUtyMonths(utyPlan.getHavKrpPlatformaPlanUtyMonths() +planDto.getHavKrpPlatformaPlanUty());
			utyPlan.setHavKrpPoluvagonPlanUtyMonths(utyPlan.getHavKrpPoluvagonPlanUtyMonths() +planDto.getHavKrpPoluvagonPlanUty());
			utyPlan.setHavKrpSisternaPlanUtyMonths(utyPlan.getHavKrpSisternaPlanUtyMonths() +planDto.getHavKrpSisternaPlanUty());
			utyPlan.setHavKrpBoshqaPlanUtyMonths(utyPlan.getHavKrpBoshqaPlanUtyMonths() +planDto.getHavKrpBoshqaPlanUty());

			utyPlan.setAndjKrpKritiPlanUtyMonths(utyPlan.getAndjKrpKritiPlanUtyMonths() + planDto.getAndjKrpKritiPlanUty());
			utyPlan.setAndjKrpPlatformaPlanUtyMonths(utyPlan.getAndjKrpPlatformaPlanUtyMonths() +planDto.getAndjKrpPlatformaPlanUty());
			utyPlan.setAndjKrpPoluvagonPlanUtyMonths(utyPlan.getAndjKrpPoluvagonPlanUtyMonths() +planDto.getAndjKrpPoluvagonPlanUty());
			utyPlan.setAndjKrpSisternaPlanUtyMonths(utyPlan.getAndjKrpSisternaPlanUtyMonths() +planDto.getAndjKrpSisternaPlanUty());
			utyPlan.setAndjKrpBoshqaPlanUtyMonths(utyPlan.getAndjKrpBoshqaPlanUtyMonths() +planDto.getAndjKrpBoshqaPlanUty());

			planUtyRepository.save(utyPlan);
		}
	}

	@Override
	public PlanUty getPlanuty() {
		Optional<PlanUty> optionalPlanUty = planUtyRepository.findById(1);
		if (optionalPlanUty.isPresent())
			return optionalPlanUty.get();
		return new PlanUty();
	}

	@Override
	public VagonTayyorUty findById(Long id) {
		return vagonTayyorUtyRepository.findById(id).get();
	}
}
