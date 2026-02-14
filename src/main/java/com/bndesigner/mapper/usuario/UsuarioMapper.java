package com.bndesigner.mapper.usuario;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import com.bndesigner.domain.entity.usuario.Usuario;
import com.bndesigner.dto.request.usuario.UsuarioCreateRequest;
import com.bndesigner.dto.request.usuario.UsuarioUpdateRequest;
import com.bndesigner.dto.response.usuario.UsuarioResponse;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UsuarioMapper {
	
	@Mapping(target = "idUsuario", ignore = true)
	Usuario toEntity(UsuarioCreateRequest request);
	
	UsuarioResponse toResponse(Usuario usuario);
	
	void updateEntityFromRequest(
			UsuarioUpdateRequest request,
			@MappingTarget Usuario usuario	
			);
}
