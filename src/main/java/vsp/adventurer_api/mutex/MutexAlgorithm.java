package vsp.adventurer_api.mutex;

import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import vsp.Application;
import vsp.adventurer_api.entities.adventurer.Adventurer;
import vsp.adventurer_api.entities.basic.User;
import vsp.adventurer_api.http.api.OurRoutes;
import vsp.adventurer_api.utility.Capabilities;
import vsp.adventurer_api.utility.LamportClock;
import vsp.adventurer_api.utility.URL;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.util.*;

import static java.util.stream.Collectors.toList;

public class MutexAlgorithm {
    private static final Logger LOG = Logger.getLogger(MutexAlgorithm.class);

    /**
     * My current state.
     */
    @Nonnull
    private MutexStates state = MutexStates.RELEASED;

    /**
     * My send request or null if I doesn't have send one.
     */
    @Nullable
    private MutexMessage myRequest;

    /**
     * Adventurer that are online and have the capability "mutex";
     */
    private List<Adventurer> adventurers = new ArrayList<>();
    private List<Adventurer> unconfirmedAnswers = new ArrayList<>();

    /**
     * My logical clock.
     */
    @Nonnull
    private final LamportClock clock = new LamportClock();

    /**
     * Queue of waiting clients for the mutex.
     */
    private final Queue<MutexMessage> queue = new LinkedList<>();

    private boolean isPrepared = false;
    private CriticalZone criticalZone;

    public void prepare() throws IOException {
        LOG.info("PREPARED MUTEX <<<");
        criticalZone = new CriticalZone();
        final Set<URL> alreadySeenUrls = new HashSet<>();
        adventurers = Application.getAdventurers().stream()
                .filter(e -> e.hasCapability(Capabilities.MUTEX))
                .filter(e -> URL.isValid(e.getUrl()))
                .filter(e -> alreadySeenUrls.add(URL.parse(e.getUrl())))
                .filter(e -> Application.client.onlineCheck(e.getUrl()))
                .collect(toList());
        isPrepared = true;
        LOG.info(" Found " + adventurers.size() + " endpoints that are ready for mutex >>>");
    }

    public void receive(@Nonnull final MutexMessage request) {
        Preconditions.checkNotNull(request, "request should not be null.");

        clock.increase("Received a mutex request");
        LOG.info("Receiving: " + request.toString());

        if (request.equals(myRequest)) {
            LOG.info(">>> received own request so we answer with ok");
            sendOK(Application.adventurer.getUrl() + OurRoutes.MUTEX);
            return;
        }
        if (StringUtils.containsIgnoreCase(request.getMsg(), "ok")) {
            LOG.info(">>> received ok-message");
            handleConfirmation(request);
            return;
        }

        switch (state) {
            case RELEASED:
                sendOK(request.getReply());
                break;
            case WANTING:
                Preconditions.checkNotNull(myRequest, "myRequest must not be null.");

                final boolean isOlderThanMyRequest = request.getTime() <= myRequest.getTime();
                final boolean areTimesEquals = request.getTime() == myRequest.getTime();

                if (isOlderThanMyRequest) {
                    sendOK(request.getReply());
                    break;
                }
//                else if (areTimesEquals) {
//                    try {
//                        if (isClientIdFromRequestSmaller(request)) {
//                            sendOK(request.getReply());
//                            break;
//                        }
//                    } catch (IOException e) {
//                        LOG.error("", e);
//                    }
//                }
            case HELD:
                queue.add(request);
                break;
            default:
                throw new IllegalArgumentException("Case not implemented!");
        }
    }

    private boolean isClientIdFromRequestSmaller(final @Nonnull MutexMessage request) throws IOException {
        final char[] our = Application.user.getName().toCharArray();
        Application.client.setDefaultURL();
        final User user = Application.client.get(Application.user, request.getUser()).getAs(User.class);
        Application.client.backToOldTarget();
        final char[] enemy = user.getName().toCharArray();

        boolean isClientIdFromRequestSmaller = true;
        for (int i = 0; i < our.length; i++) {
            if (our[i] == enemy[i]) {
                continue;
            }
            isClientIdFromRequestSmaller = our[i] > enemy[i];
        }
        return isClientIdFromRequestSmaller;
    }

    private void handleConfirmation(final @Nonnull MutexMessage request) {
        final Optional<Adventurer> first = adventurers.stream()
                .filter(e -> URL.equals(e.getUrl(), request.getReply()))
                .findFirst();
        final boolean present = first.isPresent();

        if (!present)
            throw new IllegalArgumentException("Could not find unconfirmed adventurer");

        unconfirmedAnswers.remove(first.get());
        LOG.info(">>> Removed " + first.get() + " \n >>> " + unconfirmedAnswers.stream().map(Adventurer::getUrl).collect(toList()));
    }

    private void sendOK(String url) {
        try {
            final URL parse = URL.parse(url);
            LOG.info("Sending ok message to " + parse);
            Application.sendMutexMsg(parse, new MutexMessage("OK", clock.getAndIncrease("Sending ok-message")));
        } catch (IOException e) {
            LOG.error("Can't reach " + url, e);
        }
    }

    public synchronized void requestAndAccess() {
        Preconditions.checkState(isPrepared);


        state = MutexStates.WANTING;

        final int requestTime = clock.getAndIncrease("Sending mutex requests");
        myRequest = new MutexMessage("request", requestTime);
        unconfirmedAnswers = new ArrayList<>(adventurers);

        new ArrayList<>(adventurers).forEach(e -> {
            try {
                Application.sendMutexMsg(URL.parse(e.getUrl() + OurRoutes.MUTEX), myRequest);
            } catch (IOException e1) {
                LOG.error("Can't reach " + e.getUrl() + OurRoutes.MUTEX);
                adventurers.remove(e);
                unconfirmedAnswers.remove(e);
            }
        });


        LOG.info("!!! Awaits confirmation from: \n >>>" + unconfirmedAnswers.stream().map(Adventurer::getUrl).collect(toList()));
        awaitConfirmation();

        // simulate enter critical zone
        state = MutexStates.HELD;

        criticalZone.doStuff();

        state = MutexStates.RELEASED;
        // send ok to queued adventurers
        final int okForQueuedTime;
        if (!queue.isEmpty()) {
            okForQueuedTime = clock.getAndIncrease("Sending OK after release");
            while (!queue.isEmpty()) {
                final String url = queue.poll().getReply() + OurRoutes.MUTEX;
                try {
                    Application.sendMutexMsg(URL.parse(url), new MutexMessage("OK", okForQueuedTime));
                } catch (IOException e) {
                    LOG.error("Can't reach " + url + OurRoutes.MUTEX);
                }
            }
        }
    }

    /**
     * Wait until all adventurers have been answered with ok.
     */
    private void awaitConfirmation() {
        while (!unconfirmedAnswers.isEmpty()) {
            try {
                Thread.sleep(2000);
                LOG.info("No answer from " + unconfirmedAnswers.size() + " services:" + unconfirmedAnswers.stream().map(Adventurer::getUrl).collect(toList()).toString());

                new HashSet<>(unconfirmedAnswers).forEach(e -> {
                    try {
                        LOG.info("Asking " + e.getUrl() + " for mutex state");
                        final MutexStateMessage stateMessage = Application.client.get(e.getUrl() + OurRoutes.MUTEX_STATE).getAs(MutexStateMessage.class);
                        if (MutexStates.RELEASED.asString().equals(stateMessage.getState())) {
                            unconfirmedAnswers.remove(e);
                            LOG.info(e.getUrl() + " does not send an OK but does not hold or want a lock");
                        }
                    } catch (IOException ignore) {
                        unconfirmedAnswers.remove(e);
                        LOG.info(e.getUrl() + " does not answer on " + OurRoutes.MUTEX_STATE + " so we will ignore him");
                    }

                });
            } catch (InterruptedException e) {
                LOG.error("Interrupted waiting", e);
            }
        }

    }

    public void leaveMutex() {
        criticalZone.leave();
    }

    class CriticalZone {
        private boolean held = true;

        void doStuff() {
            while (held) {
                try {
                    for (int i = 0; i < 10; i++) {
                        LOG.info("!!! In Critical zone !!!");
                        Thread.sleep(1000);
                    }
                    held = false; // TODO remove HACK; does avoid multiple threads
                } catch (InterruptedException e) {
                    LOG.error("Interrupted waiting", e);
                }
            }
            LOG.info("Leaving Critical zone...");
        }

        void leave() {
            held = false;
        }
    }

    @Nonnull
    public MutexStates getState() {
        return state;
    }

    @Nonnull
    public LamportClock getClock() {
        return clock;
    }
}
