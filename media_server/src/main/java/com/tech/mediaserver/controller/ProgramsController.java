package com.tech.mediaserver.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.pagehelper.PageInfo;
import com.tech.mediaserver.entity.OutputConfig;
import com.tech.mediaserver.entity.Programs;
import com.tech.mediaserver.entity.RespBean;
import com.tech.mediaserver.entity.Transponder;
import com.tech.mediaserver.service.AudioPidsService;
import com.tech.mediaserver.service.OutputConfigService;
import com.tech.mediaserver.service.ProgramsService;
import com.tech.mediaserver.service.SubtitleService;
import com.tech.mediaserver.service.TpService;

@RestController
@RequestMapping("/programs")
public class ProgramsController {

	public final static Logger logger = LoggerFactory.getLogger(UserController.class);

	@Autowired
	private OutputConfigService outputService;

	@Autowired
	private ProgramsService programsService;

	@Autowired
	private TpService tpService;

	@Autowired
	private AudioPidsService audioService;

	@Autowired
	private SubtitleService subtitleService;

	@RequestMapping("/getAllPrograms")
	public List<Programs> getAllPrograms() {
		return programsService.getAllPrograms();
	}

	@RequestMapping("/getProgramsByTpId")
	public List<Programs> getProgramsByTpId(Integer tp_id, Integer board_id) {

		List<Programs> programs = programsService.getProgramsByTpId(tp_id, board_id);

		return programs;
	}

	@RequestMapping("/getSearchProgramsByTpId")
	public List<Programs> getSearchProgramsByTpId(Integer tp_id, Integer board_id) {

		List<Programs> programs = programsService.getSearchProgramsByTpId(tp_id, board_id);

		return programs;
	}

	@RequestMapping("/deleteProByTpList")
	public RespBean deleteProByTpList(Integer sat_id, Integer board_id) {
		List<Transponder> transponders = tpService.getTpBySatId(sat_id, 0);
		if (transponders == null || transponders.size() == 0) {
			return RespBean.error("Failed!");
		} else {
			List<Integer> list = new ArrayList<>();

			for (Transponder transponder : transponders) {
				list.add(transponder.getId());
			}
			List<Programs> programs = programsService.getProByTpList(list, board_id);
			Integer result = programsService.deleteProByTpListAndBoardId(list, board_id);

			List<Integer> proList = new ArrayList<>();
			for (Programs program : programs) {
				proList.add(program.getId());
			}

			if (proList.size() != 0) {
				audioService.deleteAudioByProIdList(proList);
				subtitleService.deletesubtitleByProIdList(proList);
			}
			if (result == 1) {
				return RespBean.ok("Successful!");
			}
			return RespBean.error("Failed!");
		}
	}

	@RequestMapping("/deleteProByTpId")
	public Integer deleteProByTpId(Integer tp_id, Integer board_id) {
		List<Integer> list = new ArrayList<>();
		list.add(tp_id);

		List<Programs> programs = programsService.getProByTpList(list, board_id);
		Integer result = programsService.deleteProByTpListAndBoardId(list, board_id);

		List<Integer> proList = new ArrayList<>();
		for (Programs program : programs) {
			proList.add(program.getId());
		}

		if (proList.size() != 0) {
			audioService.deleteAudioByProIdList(proList);
			subtitleService.deletesubtitleByProIdList(proList);
		}
		return result;
	}

	@CrossOrigin
	@RequestMapping("/selectAllPro")
	public PageInfo<Programs> selectAllPro(Integer page, Integer size, String keyWord, Integer tp_id, Integer sat_id,
			Integer board_id) {
		List<Integer> tp_list = new ArrayList<>();
		if (tp_id == 0) {
			List<Transponder> transponders = tpService.getTpBySatId(sat_id, 0);
			tp_list = transponders.stream().map(Transponder::getId).collect(Collectors.toList());
		} else {
			tp_list.add(tp_id);
		}
		return programsService.selectAllPro(page, size, keyWord, tp_list, board_id);
	}

	@RequestMapping("/deleteProgramByBoardId")
	public RespBean deleteProgramByBoardId(Integer board_id) {

		List<Programs> programs = programsService.getProByBoardId(board_id);
		Integer result = programsService.deleteProByBoardId(board_id);

		List<Integer> proList = new ArrayList<>();
		for (Programs program : programs) {
			proList.add(program.getId());
		}

		if (proList.size() != 0) {
			audioService.deleteAudioByProIdList(proList);
			subtitleService.deletesubtitleByProIdList(proList);
		}

		return RespBean.ok("Successful!",result);
	}

	@RequestMapping("/getProByBoardId")
	public List<Programs> getProByBoardId(Integer board_id) {

		List<Programs> programs = programsService.getProByBoardId(board_id);

		return programs;

	}

	@RequestMapping("/getProByBoardIdAndTpList")
	public List<Programs> getProByBoardIdAndTpList(Integer sat_id, Integer board_id) {

		List<Transponder> transponders = tpService.getTpBySatId(sat_id, 0);
		List<Programs> programs = null;
		if (transponders == null || transponders.size() == 0) {
			return programs;
		} else {
			List<Integer> list = new ArrayList<>();

			for (Transponder transponder : transponders) {
				list.add(transponder.getId());
			}
			programs = programsService.getProByTpList(list, board_id);
		}
		return programs;
	}

	@RequestMapping("/checkProgramByBoardId")
	public boolean checkProgramByBoardId(Integer board_id) {

		List<Programs> programs = programsService.getProByBoardId(board_id);

		if (programs.size() > 0) {
			return false;
		} else {
			return true;
		}
	}

	@CrossOrigin
	@RequestMapping("/deleteProgram")
	public Integer deleteProgram(@RequestBody Integer[] ids) {

		List<Integer> proList = Arrays.asList(ids);

		// delete audio and subtitle
		if (proList.size() != 0) {
			audioService.deleteAudioByProIdList(proList);
			subtitleService.deletesubtitleByProIdList(proList);
		}

		return programsService.deleteProgramByIds(ids);
	}

	@RequestMapping("/checkProToDelete")
	public boolean checkProToDelete(@RequestBody Integer[] ids) {

		boolean result = false;

		List<Integer> proList = Arrays.asList(ids);
		List<OutputConfig> outputConfigsPro = outputService.getOutputByProIdList(proList);

		if (outputConfigsPro.size() != 0) {
			result = true;
		}

		return result;
	}

	@RequestMapping("/deleteSearchPrograms")
	public Integer deleteSearchPrograms() {
		Integer result = programsService.deleteSearchPrograms();
		return result;
	}

	@RequestMapping("/getProById")
	public Programs getProById(Integer pro_id, Integer board_id) {
		Programs programs = programsService.getProById(pro_id, board_id);
		return programs;
	}

}
