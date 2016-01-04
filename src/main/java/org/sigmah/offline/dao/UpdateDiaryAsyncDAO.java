package org.sigmah.offline.dao;

import java.util.LinkedHashMap;
import java.util.Map;

import org.sigmah.offline.indexeddb.Cursor;
import org.sigmah.offline.indexeddb.ObjectStore;
import org.sigmah.offline.indexeddb.OpenCursorRequest;
import org.sigmah.offline.indexeddb.Request;
import org.sigmah.offline.indexeddb.Store;
import org.sigmah.offline.indexeddb.Transaction;
import org.sigmah.offline.js.CommandJS;
import org.sigmah.shared.command.base.Command;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Singleton;

/**
 * Asynchronous DAO for saving and loading <code>Command</code> objects.
 * <p>
 * This DAO is used to remember every action done offline by the user. They will
 * be sent to the server during the synchronization process to update the 
 * server.
 * </p>
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
@Singleton
public class UpdateDiaryAsyncDAO extends AbstractUserDatabaseAsyncDAO<Command, CommandJS> {

	public void saveWithNegativeId(final Command t, final AsyncCallback<Integer> callback) {
		openTransaction(Transaction.Mode.READ_WRITE, new OpenTransactionHandler<Store>() {

			@Override
			public void onTransaction(final Transaction transaction) {
				generateNegativeId(new AsyncCallback<Integer>() {

					@Override
					public void onFailure(Throwable caught) {
						callback.onFailure(caught);
					}

					@Override
					public void onSuccess(final Integer generatedId) {
						final ObjectStore commandObjectStore = transaction.getObjectStore(Store.COMMAND);
						
						final CommandJS commandJS = CommandJS.toJavaScript(t);
						commandJS.setId(generatedId);
						
						commandObjectStore.add(commandJS).addCallback(new AsyncCallback<Request>() {

							@Override
							public void onFailure(Throwable caught) {
								callback.onFailure(caught);
							}

							@Override
							public void onSuccess(Request result) {
								callback.onSuccess(generatedId);
							}
						});
					}
				}, transaction);
			}
		});
	}

	public void getAll(final AsyncCallback<Map<Integer, Command>> callback) {
        openTransaction(Transaction.Mode.READ_ONLY, new OpenTransactionHandler<Store>() {

            @Override
            public void onTransaction(Transaction<Store> transaction) {
                final LinkedHashMap<Integer, Command> commands = new LinkedHashMap<Integer, Command>();
				
				final ObjectStore commandObjectStore = transaction.getObjectStore(Store.COMMAND);
				final OpenCursorRequest cursorRequest = commandObjectStore.openCursor();
                
                cursorRequest.addCallback(new AsyncCallback<Request>() {

                    @Override
                    public void onFailure(Throwable caught) {
                        callback.onFailure(caught);
                    }

                    @Override
                    public void onSuccess(Request request) {
                        final Cursor cursor = cursorRequest.getResult();
						if(cursor != null) {
							final CommandJS commandJS = (CommandJS) cursor.getValue();
							commands.put(commandJS.getId(), commandJS.toCommand());
							cursor.next();
							
						} else {
							callback.onSuccess(commands);
						}
                    }
                    
                });
            }
        });
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Store getRequiredStore() {
		return Store.COMMAND;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CommandJS toJavaScriptObject(Command t) {
		return CommandJS.toJavaScript(t);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Command toJavaObject(CommandJS js) {
		return js.toCommand();
	}
	
}
