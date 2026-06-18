    package com.olamide.miniwalletapi.Service.ServiceImpl;
    
    import com.olamide.miniwalletapi.Configuration.SecurityUtils;
    import com.olamide.miniwalletapi.DTO.*;
    import com.olamide.miniwalletapi.Exceptions.InvalidTransactionException;
    import com.olamide.miniwalletapi.Exceptions.WalletDeactivatedException;
    import com.olamide.miniwalletapi.Exceptions.WalletNotFoundException;
    import com.olamide.miniwalletapi.Models.Transaction;
    import com.olamide.miniwalletapi.Models.User;
    import com.olamide.miniwalletapi.Models.Wallet;
    import com.olamide.miniwalletapi.Repository.IdempotencyRecordRepository;
    import com.olamide.miniwalletapi.Repository.TransactionRepository;
    import com.olamide.miniwalletapi.Repository.WalletRepository;
    import com.olamide.miniwalletapi.Service.TransactionService;
    import com.olamide.miniwalletapi.TransactionType;
    import org.springframework.data.domain.Pageable;
    import org.springframework.data.domain.Slice;
    import org.springframework.stereotype.Service;
    import org.springframework.transaction.annotation.Transactional;

    import java.util.Optional;

    @Service
    public class TransactionServiceImpl implements TransactionService {
        private final TransactionRepository transactionRepository;
        private final WalletRepository walletRepository;
        private final IdempotencyRecordServiceImpl idempotencyRecordService;
    
        public TransactionServiceImpl(TransactionRepository transactionRepository, WalletRepository walletRepository,IdempotencyRecordServiceImpl idempotencyRecordService) {
            this.transactionRepository = transactionRepository;
            this.walletRepository = walletRepository;
            this.idempotencyRecordService = idempotencyRecordService;
        }


        private Wallet getAuthenticatedWallet()  {
            User currentUser = SecurityUtils.getAuthenticatedUser();
            return walletRepository.findByUser(currentUser)
                    .orElseThrow(() -> new WalletNotFoundException("No wallet found for authenticated user"));


        }
        private TransactionResponseDTO save(Transaction transaction) {
            Transaction saved = transactionRepository.save(transaction);
            return new TransactionResponseDTO(saved.getSourceWalletId(),
                    saved.getDestinationWalletId(),
                                                saved.getAmount(),
                                                saved.getType(),
                                                saved.getTimestamp());
        }

        @Override
        @Transactional
        public TransactionResponseDTO deposit(DepositRequestDTO request,String idempotencyKey) {
            Optional<TransactionResponseDTO> existingResponse = idempotencyRecordService.findExistingResponse(idempotencyKey);
            if(existingResponse.isPresent()) {
                return existingResponse.get();
            }

            Wallet found = getAuthenticatedWallet();
            if (!found.isActive()) {
                throw new WalletDeactivatedException("Cannot deposit:  wallet is deactivated.");
            }

                found.credit(request.amount());

                Transaction newTransaction = new Transaction(TransactionType.DEPOSIT,request.amount(),null,found.getId());
                walletRepository.save(found);
                TransactionResponseDTO result  = save(newTransaction);
                idempotencyRecordService.saveResponse(idempotencyKey,result,newTransaction.getId());
                return result;


        }

        @Override
        @Transactional
        public TransactionResponseDTO withdraw(WithdrawRequestDTO request,String idempotencyKey) {
            Optional<TransactionResponseDTO> existingResponse = idempotencyRecordService.findExistingResponse(idempotencyKey);
            if(existingResponse.isPresent()) {
                return existingResponse.get();
            }
            Wallet found = getAuthenticatedWallet();
            if (!found.isActive()) {
                throw new WalletDeactivatedException("Cannot withdraw: Source wallet is deactivated.");
            }
            found.debit(request.amount());
            Transaction newTransaction = new Transaction(TransactionType.WITHDRAW,request.amount(), found.getId(), null);
            walletRepository.save(found);

            TransactionResponseDTO result  = save(newTransaction);
            idempotencyRecordService.saveResponse(idempotencyKey,result,newTransaction.getId());
            return result;


        }

        @Override
        @Transactional
        public TransactionResponseDTO transfer(TransferRequestDTO transferRequest,String idempotencyKey) {
            Optional<TransactionResponseDTO> existingResponse = idempotencyRecordService.findExistingResponse(idempotencyKey);
            if(existingResponse.isPresent()) {
                return existingResponse.get();
            }
            Wallet sender = getAuthenticatedWallet();
            if (sender.getId().equals(transferRequest.destinationWalletId())) {
                throw new InvalidTransactionException("Transfer failed: Source and destination wallets cannot be the same account.");

            }
            Wallet receiver = walletRepository.findById(transferRequest.destinationWalletId())
                    .orElseThrow(() -> new WalletNotFoundException("Destination Wallet not Found"));
            if (!sender.isActive()) {
                throw new WalletDeactivatedException("Transfer failed: Sender account is deactivated.");
            }
            if (!receiver.isActive()) {
                throw new WalletDeactivatedException("Transfer failed: Destination account is deactivated.");
            }
            sender.debit(transferRequest.amount());
            receiver.credit(transferRequest.amount());
    
            Transaction transfer = new Transaction(TransactionType.TRANSFER,
                    transferRequest.amount(),
                    sender.getId(),
                    transferRequest.destinationWalletId());
            walletRepository.save(sender);
            walletRepository.save(receiver);
            TransactionResponseDTO result  = save(transfer);
            idempotencyRecordService.saveResponse(idempotencyKey,result,transfer.getId());
            return result;
    
    
        }

        @Override
        public TransactionResponseDTO findById(Long Id) {
            return (transactionRepository.findById(Id).map(
                    this::mapToDTO
                    )
            ).orElseThrow(() -> new WalletNotFoundException("No transaction with id " + Id));
        }

        @Override
        public PagedResponseDTO<TransactionResponseDTO> getTransactionHistory(Long walletId, Pageable pageable) {
            Slice<TransactionResponseDTO> slice =  transactionRepository.findAllByWalletId(walletId,pageable)
                    .map(this::mapToDTO);
            return toPagedResponse(slice);

        }


        @Override
        public PagedResponseDTO<TransactionResponseDTO> getMyTransactionHistory(Pageable pageable) {
            Wallet wallet = getAuthenticatedWallet();
            Slice<TransactionResponseDTO> slice=  transactionRepository.findAllByWalletId(wallet.getId(),pageable)
                    .map(this::mapToDTO);
            return toPagedResponse(slice);
        }

        private PagedResponseDTO<TransactionResponseDTO> toPagedResponse(Slice<TransactionResponseDTO> slice) {
            return new PagedResponseDTO<TransactionResponseDTO>(
                    slice.getContent(),
                    slice.hasNext(),
                    slice.getNumber(),
                    slice.getSize()
            );
        }


        private TransactionResponseDTO mapToDTO(Transaction transaction) {
            return new TransactionResponseDTO(
                    transaction.getSourceWalletId(),
                    transaction.getDestinationWalletId(),
                    transaction.getAmount(),
                    transaction.getType(),
                    transaction.getTimestamp()
            );
        }
    }
