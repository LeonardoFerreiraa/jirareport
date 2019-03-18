package br.com.jiratorio.service

import br.com.jiratorio.domain.Account
import br.com.jiratorio.domain.entity.Board
import br.com.jiratorio.domain.request.CreateBoardRequest
import br.com.jiratorio.domain.request.UpdateBoardRequest
import br.com.jiratorio.domain.response.BoardDetailsResponse
import br.com.jiratorio.domain.response.BoardResponse
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface BoardService {

    fun findAll(pageable: Pageable, board: Board, currentUser: Account): Page<BoardResponse>

    fun create(createBoardRequest: CreateBoardRequest): Long

    fun delete(id: Long, username: String)

    fun findById(id: Long): Board

    fun update(boardId: Long, updateBoardRequest: UpdateBoardRequest)

    fun findStatusFromBoardInJira(board: Board): Set<String>

    fun findStatusFromBoardInJira(boardId: Long): Set<String>

    fun findDetailsById(id: Long): BoardDetailsResponse

    fun findAllOwners(): Set<String>

}
