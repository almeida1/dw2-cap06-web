package com.fatec.sigx.controller;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fatec.sigx.model.Cliente;
import com.fatec.sigx.model.ClienteDTO;
import com.fatec.sigx.model.Endereco;
import com.fatec.sigx.services.MantemCliente;

@RestController
@RequestMapping("/api/v1/clientes")
/*
 * Trata as requisicoes HTTP enviadas pelo usuario do servico
 */
public class APIClienteController {
	@Autowired
	MantemCliente servico;

	Logger logger = LogManager.getLogger(this.getClass());

	@PostMapping
	public ResponseEntity<Object> saveCliente(@RequestBody @Valid ClienteDTO clienteDTO, BindingResult result) {
		if (result.hasErrors()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result.getFieldError().getDefaultMessage());
		}
		if (servico.consultaPorCpf(clienteDTO.getCpf()).isPresent()) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body("CPF já cadastrado");
		}
		if (servico.validaData(clienteDTO.getDataNascimento()) == false) { // data invalida ex 31/02/XXXX
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Data inválida");
		}
		if (servico.obtemEndereco(clienteDTO.getCep()).isEmpty()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("CEP invalido");
		}
		try {
			logger.info(">>>>>> controller dados validos post enviado");
			return ResponseEntity.status(HttpStatus.CREATED).body(servico.save(clienteDTO.retornaUmCliente()));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Exception nao esperado contate o administrador.");
		}
		
	}

	@GetMapping
	public ResponseEntity<List<Cliente>> consultaTodos() {
		return ResponseEntity.status(HttpStatus.OK).body(servico.consultaTodos());
	}

	@GetMapping("/{id}/id")
	public ResponseEntity<Object> consultaPorId(@PathVariable(value = "id") Long id) {
		Optional<Cliente> cliente = servico.consultaPorId(id);
		if (servico.consultaPorId(id).isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Id não encontrado.");
		} else {
			return ResponseEntity.status(HttpStatus.OK).body(cliente.get());
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Object> deletePorId(@PathVariable(value = "id") Long id) {
		Optional<Cliente> cliente = servico.consultaPorId(id);
		if (cliente.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Id não encontrado.");
		} else {
			servico.delete(cliente.get().getId());
			return ResponseEntity.status(HttpStatus.OK).body("Cliente excluido");
		}
	}

	@PutMapping("/{id}")
	public ResponseEntity<Object> atualiza(@PathVariable(value = "id") Long id,
			@RequestBody @Valid ClienteDTO clienteDTO) {
		logger.info(">>>>>> controller consultaPorId chamado");
		Optional<Cliente> c = servico.consultaPorId(id);
		if (c.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Id não encontrado.");
		}

		if (servico.obtemEndereco(clienteDTO.getCep()).isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("CEP não localizado.");
		}
		Cliente umCliente = clienteDTO.retornaUmCliente();
		umCliente.setId(id);
		if (servico.atualiza(umCliente).isPresent()) {
			return ResponseEntity.status(HttpStatus.OK).body("Informações de cliente atualizada");
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Informações invalidas");
		}

	}
}
