package com.bndesigner.service.metodopagamento;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bndesigner.domain.entity.metodopagamento.MetodoPagamento;
import com.bndesigner.dto.request.metodopagamento.MetodoPagamentoCreateRequest;
import com.bndesigner.dto.response.metodopagamento.MetodoPagamentoResponse;
import com.bndesigner.dto.request.metodopagamento.MetodoPagamentoUpdateRequest;
import com.bndesigner.mapper.metodopagamento.MetodoPagamentoMapper;
import com.bndesigner.repository.metodopagamento.MetodoPagamentoRepository;
import com.bndesigner.service.metodopagamento.impl.MetodoPagamentoService;
import com.bndesigner.util.EntityLookup;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MetodoPagamentoServiceImpl implements MetodoPagamentoService {

    private final MetodoPagamentoRepository metodoPagamentoRepository;
    private final MetodoPagamentoMapper metodoPagamentoMapper;

    @Override
    @Transactional
    public MetodoPagamentoResponse criar(
            MetodoPagamentoCreateRequest request) {

        MetodoPagamento metodoPagamento =
                metodoPagamentoMapper.toEntity(request);

        if (metodoPagamento.getAtivo() == null) {
            metodoPagamento.setAtivo(true);
        }

        metodoPagamento =
                metodoPagamentoRepository.save(metodoPagamento);

        return metodoPagamentoMapper.toResponse(metodoPagamento);
    }

    @Override
    @Transactional(readOnly = true)
    public MetodoPagamentoResponse buscarPorId(Long id) {

        MetodoPagamento metodoPagamento =
                EntityLookup.buscarOuLancar(
                        metodoPagamentoRepository,
                        id,
                        "Método de pagamento");

        return metodoPagamentoMapper.toResponse(metodoPagamento);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MetodoPagamentoResponse> listar() {

        return metodoPagamentoRepository.findAll()
                .stream()
                .map(metodoPagamentoMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public MetodoPagamentoResponse atualizar(
            Long id,
            MetodoPagamentoUpdateRequest request) {

        MetodoPagamento metodoPagamento =
                EntityLookup.buscarOuLancar(
                        metodoPagamentoRepository,
                        id,
                        "Método de pagamento");

        metodoPagamentoMapper.updateEntityFromRequest(
                request,
                metodoPagamento);

        return metodoPagamentoMapper.toResponse(metodoPagamento);
    }

    @Override
    @Transactional
    public void desativar(Long id) {

        MetodoPagamento metodoPagamento =
                EntityLookup.buscarOuLancar(
                        metodoPagamentoRepository,
                        id,
                        "Método de pagamento");

        metodoPagamento.setAtivo(false);
    }
}