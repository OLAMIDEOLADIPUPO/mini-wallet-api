    package com.olamide.miniwalletapi.Service.ServiceImpl;
    
    import com.olamide.miniwalletapi.DTO.DepositRequestDTO;
    import com.olamide.miniwalletapi.DTO.TransactionResponseDTO;
    import com.olamide.miniwalletapi.DTO.TransferRequestDTO;
    import com.olamide.miniwalletapi.DTO.WithdrawRequestDTO;
    import com.olamide.miniwalletapi.Exceptions.InvalidTransactionException;
    import com.olamide.miniwalletapi.Exceptions.WalletDeactivatedException;
    import com.olamide.miniwalletapi.Exceptions.WalletNotFoundException;
    import com.olamide.miniwalletapi.Models.Transaction;
    import com.olamide.miniwalletapi.Models.User;
    import com.olamide.miniwalletapi.Models.Wallet;
    import com.olamide.miniwalletapi.Repository.TransactionRepository;
    import com.olamide.miniwalletapi.Repository.WalletRepository;
    import com.olamide.miniwalletapi.Service.TransactionService;
    import com.olamide.miniwalletapi.TransactionType;
    import org.springframework.security.core.Authentication;
    import org.springframework.security.core.context.SecurityContextHolder;
    import org.springframework.stereotype.Service;
    import org.springframework.transaction.annotation.Transactional;
    import org.springframework.security.access.AccessDeniedException;

    import java.util.List;

    @Service
    public class TransactionServiceImpl implements TransactionService {
        private final TransactionRepository transactionRepository;
        private final WalletRepository walletRepository;
    
        public TransactionServiceImpl(TransactionRepository transactionRepository, WalletRepository walletRepository) {
            this.transactionRepository = transactionRepository;
            this.walletRepository = walletRepository;
        }


        private Wallet getAuthenticatedWallet()  {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if(authentication == null || !authentication.isAuthenticated()){
                throw new AccessDeniedException("You are not authorized to perform this action.");
            }

            Object principal =  authentication.getPrincipal();
            if(!(principal instanceof User currentUser)){
                throw new AccessDeniedException("Unexpected principal type");

            }

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
        public TransactionResponseDTO deposit(DepositRequestDTO request) {
                Wallet found = getAuthenticatedWallet();
            if (!found.isActive()) {
                throw new WalletDeactivatedException("Cannot deposit:  wallet is deactivated.");
            }

                found.credit(request.amount());
    
                Transaction newTransaction = new Transaction(TransactionType.DEPOSIT,request.amount(),null,found.getId());
                walletRepository.save(found);
                return save(newTransaction);
    

        }

        @Override
        @Transactional
        public TransactionResponseDTO withdraw(WithdrawRequestDTO request) {
            Wallet found = getAuthenticatedWallet();
            if (!found.isActive()) {
                throw new WalletDeactivatedException("Cannot withdraw: Source wallet is deactivated.");
            }
            found.debit(request.amount());
            Transaction newTransaction = new Transaction(TransactionType.WITHDRAW,request.amount(), found.getId(), null);
            walletRepository.save(found);
    
            return save(newTransaction);
    
    
        }

        @Override
        @Transactional
        public TransactionResponseDTO transfer(TransferRequestDTO transferRequest) {
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
            return save(transfer);
    
    
        }

        @Override
        public TransactionResponseDTO findById(Long Id) {
            return (transactionRepository.findById(Id).map(
                    transaction -> new TransactionResponseDTO(
                            transaction.getSourceWalletId(),
                            transaction.getDestinationWalletId(),
                            transaction.getAmount(),
                            transaction.getType(),
                            transaction.getTimestamp()
                    )
            ).orElseThrow(() -> new WalletNotFoundException("No transaction with id " + Id)));
        }


        @Override
        public List<TransactionResponseDTO> getTransactionHistory() {
            Wallet wallet = getAuthenticatedWallet();
            return transactionRepository.findAllByWalletId(wallet.getId())
                    .stream()
                    .map(transaction -> new TransactionResponseDTO(
                            transaction.getSourceWalletId(),
                            transaction.getDestinationWalletId(),
                            transaction.getAmount(),
                            transaction.getType(),
                            transaction.getTimestamp()
                    ))
                    .toList();
        }
    }
